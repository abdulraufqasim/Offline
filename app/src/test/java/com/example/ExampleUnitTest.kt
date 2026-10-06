package com.example

import com.example.data.model.AppLanguage
import com.example.data.model.PhraseCategory
import com.example.data.repository.PhrasebookRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun appLanguages_rtlConfiguration_isCorrect() {
        assertTrue("Arabic must be RTL", AppLanguage.ARABIC.isRtl)
        assertTrue("Urdu must be RTL", AppLanguage.URDU.isRtl)
        assertFalse("English must not be RTL", AppLanguage.ENGLISH.isRtl)
        assertFalse("Turkish must not be RTL", AppLanguage.TURKISH.isRtl)
    }

    @Test
    fun appLanguages_fromCode_resolvesCorrectly() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.TURKISH, AppLanguage.fromCode("tr"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.fromCode("ar"))
        assertEquals(AppLanguage.URDU, AppLanguage.fromCode("ur"))
    }

    @Test
    fun phrasebook_containsAllRequiredCategories() {
        val phrases = PhrasebookRepository.allPhrases
        assertTrue(phrases.isNotEmpty())

        val categoriesInRepo = phrases.map { it.category }.toSet()
        for (category in PhraseCategory.entries) {
            assertTrue("Category $category should have phrases", categoriesInRepo.contains(category))
        }
    }

    @Test
    fun phrasebook_translations_arePopulatedForAllLanguages() {
        for (phrase in PhrasebookRepository.allPhrases) {
            assertTrue("English phrase must not be empty", phrase.english.isNotBlank())
            assertTrue("Turkish phrase must not be empty", phrase.turkish.isNotBlank())
            assertTrue("Arabic phrase must not be empty", phrase.arabic.isNotBlank())
            assertTrue("Urdu phrase must not be empty", phrase.urdu.isNotBlank())
            assertNotNull(phrase.getTextForLanguage(AppLanguage.TURKISH))
        }
    }
}
