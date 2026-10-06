package com.example.data.repository

import com.example.data.local.TranslationDao
import com.example.data.local.TranslationEntity
import kotlinx.coroutines.flow.Flow

class TranslationRepository(private val dao: TranslationDao) {

    val history: Flow<List<TranslationEntity>> = dao.getAllHistory()
    val favorites: Flow<List<TranslationEntity>> = dao.getFavorites()

    suspend fun saveTranslation(
        sourceLangCode: String,
        targetLangCode: String,
        sourceText: String,
        translatedText: String
    ): Long {
        val existing = dao.findExisting(sourceLangCode, targetLangCode, sourceText)
        return if (existing != null) {
            val updated = existing.copy(
                translatedText = translatedText,
                timestamp = System.currentTimeMillis()
            )
            dao.update(updated)
            existing.id
        } else {
            val newEntry = TranslationEntity(
                sourceLangCode = sourceLangCode,
                targetLangCode = targetLangCode,
                sourceText = sourceText,
                translatedText = translatedText,
                timestamp = System.currentTimeMillis(),
                isFavorite = false
            )
            dao.insert(newEntry)
        }
    }

    suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory(keepFavorites: Boolean = true) {
        if (keepFavorites) {
            dao.clearHistoryNonFavorites()
        } else {
            dao.clearAll()
        }
    }

    suspend fun findExisting(
        sourceLangCode: String,
        targetLangCode: String,
        sourceText: String
    ): TranslationEntity? {
        return dao.findExisting(sourceLangCode, targetLangCode, sourceText)
    }
}
