package com.example.data.repository

import com.example.data.model.AppLanguage
import com.example.data.model.ModelDownloadStatus
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MlKitTranslationService {

    private val modelManager: RemoteModelManager = RemoteModelManager.getInstance()
    private val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()

    private val _downloadStates = MutableStateFlow<Map<AppLanguage, ModelDownloadStatus>>(
        AppLanguage.entries.associateWith { ModelDownloadStatus.NotDownloaded }
    )
    val downloadStates: StateFlow<Map<AppLanguage, ModelDownloadStatus>> = _downloadStates.asStateFlow()

    private val translatorCache = ConcurrentHashMap<String, Translator>()

    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            if (cont.isActive) cont.resume(result)
        }
        addOnFailureListener { exception ->
            if (cont.isActive) cont.resumeWithException(exception)
        }
        addOnCanceledListener {
            if (cont.isActive) cont.cancel()
        }
    }

    suspend fun refreshDownloadedStatus() = withContext(Dispatchers.IO) {
        val currentStates = _downloadStates.value.toMutableMap()
        for (lang in AppLanguage.entries) {
            val isDownloading = currentStates[lang] is ModelDownloadStatus.Downloading
            if (isDownloading) continue

            val model = TranslateRemoteModel.Builder(lang.mlKitCode).build()
            val isDownloaded = try {
                modelManager.isModelDownloaded(model).awaitTask()
            } catch (_: Exception) {
                false
            }
            currentStates[lang] = if (isDownloaded) {
                ModelDownloadStatus.Downloaded
            } else {
                ModelDownloadStatus.NotDownloaded
            }
        }
        _downloadStates.value = currentStates
    }

    suspend fun isModelDownloaded(language: AppLanguage): Boolean = withContext(Dispatchers.IO) {
        val model = TranslateRemoteModel.Builder(language.mlKitCode).build()
        try {
            modelManager.isModelDownloaded(model).awaitTask()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun downloadLanguageModel(language: AppLanguage): Result<Unit> = withContext(Dispatchers.IO) {
        _downloadStates.value = _downloadStates.value.toMutableMap().apply {
            put(language, ModelDownloadStatus.Downloading())
        }

        try {
            val model = TranslateRemoteModel.Builder(language.mlKitCode).build()
            val conditions = DownloadConditions.Builder().build()
            modelManager.download(model, conditions).awaitTask()

            _downloadStates.value = _downloadStates.value.toMutableMap().apply {
                put(language, ModelDownloadStatus.Downloaded)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val userFriendlyMessage = when {
                e.message?.contains("network", ignoreCase = true) == true ||
                e.message?.contains("connect", ignoreCase = true) == true ||
                e.message?.contains("internet", ignoreCase = true) == true ->
                    "Network error. Please connect to Wi-Fi/data to download ${language.displayName}."
                e.message?.contains("storage", ignoreCase = true) == true ||
                e.message?.contains("space", ignoreCase = true) == true ->
                    "Insufficient storage to download ${language.displayName} pack (~30MB)."
                else -> "Failed to download ${language.displayName}: ${e.localizedMessage ?: "Please try again"}"
            }
            _downloadStates.value = _downloadStates.value.toMutableMap().apply {
                put(language, ModelDownloadStatus.Error(userFriendlyMessage))
            }
            Result.failure(Exception(userFriendlyMessage, e))
        }
    }

    suspend fun deleteLanguageModel(language: AppLanguage): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val model = TranslateRemoteModel.Builder(language.mlKitCode).build()
            modelManager.deleteDownloadedModel(model).awaitTask()

            // Invalidate cached translators using this language
            val iterator = translatorCache.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.contains(language.mlKitCode)) {
                    entry.value.close()
                    iterator.remove()
                }
            }

            _downloadStates.value = _downloadStates.value.toMutableMap().apply {
                put(language, ModelDownloadStatus.NotDownloaded)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun identifyLanguage(text: String): AppLanguage? = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext null
        try {
            val langCode = languageIdentifier.identifyLanguage(text).awaitTask()
            if (langCode != "und") {
                AppLanguage.fromCode(langCode)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun translate(
        text: String,
        sourceLanguage: AppLanguage,
        targetLanguage: AppLanguage
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return@withContext Result.success("")
        if (sourceLanguage == targetLanguage) return@withContext Result.success(trimmed)

        // Verify models are downloaded
        val sourceModel = TranslateRemoteModel.Builder(sourceLanguage.mlKitCode).build()
        val targetModel = TranslateRemoteModel.Builder(targetLanguage.mlKitCode).build()

        val sourceDownloaded = try {
            modelManager.isModelDownloaded(sourceModel).awaitTask()
        } catch (_: Exception) {
            false
        }
        val targetDownloaded = try {
            modelManager.isModelDownloaded(targetModel).awaitTask()
        } catch (_: Exception) {
            false
        }

        if (!sourceDownloaded || !targetDownloaded) {
            val missing = buildList {
                if (!sourceDownloaded) add(sourceLanguage.displayName)
                if (!targetDownloaded) add(targetLanguage.displayName)
            }.joinToString(" and ")
            return@withContext Result.failure(
                IllegalStateException("Offline pack for $missing is not downloaded. Please download it first.")
            )
        }

        try {
            val cacheKey = "${sourceLanguage.mlKitCode}->${targetLanguage.mlKitCode}"
            val translator = translatorCache.computeIfAbsent(cacheKey) {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLanguage.mlKitCode)
                    .setTargetLanguage(targetLanguage.mlKitCode)
                    .build()
                Translation.getClient(options)
            }

            val result = translator.translate(trimmed).awaitTask()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cleanUp() {
        for (translator in translatorCache.values) {
            try {
                translator.close()
            } catch (_: Exception) {}
        }
        translatorCache.clear()
        try {
            languageIdentifier.close()
        } catch (_: Exception) {}
    }
}
