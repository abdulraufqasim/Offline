package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.TranslationEntity
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.AppLanguage
import com.example.data.model.ModelDownloadStatus
import com.example.data.model.ThemeMode
import com.example.data.repository.MlKitTranslationService
import com.example.data.repository.TranslationRepository
import com.example.util.TtsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val title: String) {
    TRANSLATE("Translate"),
    PHRASEBOOK("Phrasebook"),
    SAVED("Saved & History"),
    SETTINGS("Language Packs")
}

class MainViewModel(
    private val translationService: MlKitTranslationService,
    private val translationRepo: TranslationRepository,
    private val preferencesRepo: UserPreferencesRepository,
    val ttsManager: TtsManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesRepo.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val isOnboardingCompleted: StateFlow<Boolean> = preferencesRepo.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val downloadStates: StateFlow<Map<AppLanguage, ModelDownloadStatus>> =
        translationService.downloadStates

    private val _currentDestination = MutableStateFlow(AppDestination.TRANSLATE)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Translation State
    private val _sourceLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val sourceLanguage: StateFlow<AppLanguage> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(AppLanguage.TURKISH)
    val targetLanguage: StateFlow<AppLanguage> = _targetLanguage.asStateFlow()

    private val _sourceText = MutableStateFlow("")
    val sourceText: StateFlow<String> = _sourceText.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _translationError = MutableStateFlow<String?>(null)
    val translationError: StateFlow<String?> = _translationError.asStateFlow()

    private val _detectedLanguage = MutableStateFlow<AppLanguage?>(null)
    val detectedLanguage: StateFlow<AppLanguage?> = _detectedLanguage.asStateFlow()

    private val _isCurrentFavorite = MutableStateFlow(false)
    val isCurrentFavorite: StateFlow<Boolean> = _isCurrentFavorite.asStateFlow()

    private var currentSavedId: Long? = null
    private var debounceJob: Job? = null

    // History and Favorites from Room
    val history: StateFlow<List<TranslationEntity>> = translationRepo.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<TranslationEntity>> = translationRepo.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            translationService.refreshDownloadedStatus()
        }
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun setSourceLanguage(language: AppLanguage) {
        if (_sourceLanguage.value == language) return
        if (_targetLanguage.value == language) {
            _targetLanguage.value = _sourceLanguage.value
        }
        _sourceLanguage.value = language
        saveLanguagesPref()
        if (_sourceText.value.isNotBlank()) {
            translateNow()
        }
    }

    fun setTargetLanguage(language: AppLanguage) {
        if (_targetLanguage.value == language) return
        if (_sourceLanguage.value == language) {
            _sourceLanguage.value = _targetLanguage.value
        }
        _targetLanguage.value = language
        saveLanguagesPref()
        if (_sourceText.value.isNotBlank()) {
            translateNow()
        }
    }

    fun swapLanguages() {
        val temp = _sourceLanguage.value
        _sourceLanguage.value = _targetLanguage.value
        _targetLanguage.value = temp
        saveLanguagesPref()

        // Swap texts if we already have translated text
        val currentTranslated = _translatedText.value
        if (currentTranslated.isNotBlank()) {
            _sourceText.value = currentTranslated
            translateNow()
        }
    }

    private fun saveLanguagesPref() {
        viewModelScope.launch {
            preferencesRepo.setLanguages(_sourceLanguage.value, _targetLanguage.value)
        }
    }

    fun onSourceTextChanged(newText: String) {
        _sourceText.value = newText
        _translationError.value = null
        if (newText.isBlank()) {
            _translatedText.value = ""
            _detectedLanguage.value = null
            _isCurrentFavorite.value = false
            currentSavedId = null
            return
        }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            // Optional language auto-detection check
            val detected = translationService.identifyLanguage(newText)
            _detectedLanguage.value = detected

            // Debounced auto-translate after 600ms pause
            delay(600)
            translateNow()
        }
    }

    fun applyDetectedLanguage() {
        val detected = _detectedLanguage.value ?: return
        if (detected != _sourceLanguage.value) {
            setSourceLanguage(detected)
        }
    }

    fun clearSourceText() {
        _sourceText.value = ""
        _translatedText.value = ""
        _translationError.value = null
        _detectedLanguage.value = null
        _isCurrentFavorite.value = false
        currentSavedId = null
    }

    fun translateNow() {
        val textToTranslate = _sourceText.value.trim()
        if (textToTranslate.isEmpty()) {
            _translatedText.value = ""
            return
        }

        viewModelScope.launch {
            _isTranslating.value = true
            _translationError.value = null

            val result = translationService.translate(
                text = textToTranslate,
                sourceLanguage = _sourceLanguage.value,
                targetLanguage = _targetLanguage.value
            )

            result.fold(
                onSuccess = { translated ->
                    _translatedText.value = translated
                    _isTranslating.value = false

                    if (translated.isNotBlank()) {
                        // Save to Room history
                        val id = translationRepo.saveTranslation(
                            sourceLangCode = _sourceLanguage.value.code,
                            targetLangCode = _targetLanguage.value.code,
                            sourceText = textToTranslate,
                            translatedText = translated
                        )
                        currentSavedId = id
                        val existing = translationRepo.findExisting(
                            _sourceLanguage.value.code,
                            _targetLanguage.value.code,
                            textToTranslate
                        )
                        _isCurrentFavorite.value = existing?.isFavorite == true
                    }
                },
                onFailure = { error ->
                    _isTranslating.value = false
                    _translationError.value = error.localizedMessage ?: "Translation failed."
                }
            )
        }
    }

    fun toggleFavoriteCurrent() {
        val newFav = !_isCurrentFavorite.value
        _isCurrentFavorite.value = newFav
        val id = currentSavedId
        if (id != null) {
            viewModelScope.launch {
                translationRepo.setFavorite(id, newFav)
            }
        }
    }

    fun loadFromHistoryOrPhrasebook(
        source: String,
        sourceLang: AppLanguage,
        target: String,
        targetLang: AppLanguage
    ) {
        _sourceLanguage.value = sourceLang
        _targetLanguage.value = targetLang
        _sourceText.value = source
        _translatedText.value = target
        _translationError.value = null
        _currentDestination.value = AppDestination.TRANSLATE

        viewModelScope.launch {
            val existing = translationRepo.findExisting(sourceLang.code, targetLang.code, source)
            if (existing != null) {
                currentSavedId = existing.id
                _isCurrentFavorite.value = existing.isFavorite
            } else {
                val newId = translationRepo.saveTranslation(sourceLang.code, targetLang.code, source, target)
                currentSavedId = newId
                _isCurrentFavorite.value = false
            }
        }
    }

    fun toggleFavoriteItem(item: TranslationEntity) {
        viewModelScope.launch {
            val newStatus = !item.isFavorite
            translationRepo.setFavorite(item.id, newStatus)
            if (item.id == currentSavedId) {
                _isCurrentFavorite.value = newStatus
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            translationRepo.deleteById(id)
            if (id == currentSavedId) {
                _isCurrentFavorite.value = false
                currentSavedId = null
            }
        }
    }

    fun clearHistory(keepFavorites: Boolean) {
        viewModelScope.launch {
            translationRepo.clearHistory(keepFavorites)
        }
    }

    fun downloadModel(language: AppLanguage) {
        viewModelScope.launch {
            translationService.downloadLanguageModel(language)
        }
    }

    fun deleteModel(language: AppLanguage) {
        viewModelScope.launch {
            translationService.deleteLanguageModel(language)
        }
    }

    fun refreshModelStatuses() {
        viewModelScope.launch {
            translationService.refreshDownloadedStatus()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepo.setOnboardingCompleted(true)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepo.setThemeMode(mode)
        }
    }

    fun speak(text: String, language: AppLanguage) {
        if (text.isNotBlank()) {
            ttsManager.speak(text, language)
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()
        translationService.cleanUp()
        ttsManager.shutdown()
    }

    class Factory(
        private val translationService: MlKitTranslationService,
        private val translationRepo: TranslationRepository,
        private val preferencesRepo: UserPreferencesRepository,
        private val ttsManager: TtsManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                translationService,
                translationRepo,
                preferencesRepo,
                ttsManager
            ) as T
        }
    }
}
