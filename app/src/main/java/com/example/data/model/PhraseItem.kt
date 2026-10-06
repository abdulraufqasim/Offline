package com.example.data.model

enum class PhraseCategory(val title: String, val iconName: String) {
    GREETINGS("Greetings", "WavingHand"),
    DINING("Food & Dining", "Restaurant"),
    TRANSPORT("Transport", "DirectionsBus"),
    SHOPPING("Shopping & Bazaars", "ShoppingBag"),
    HOTEL("Hotel & Stay", "Hotel"),
    EMERGENCY("Emergency & Health", "LocalHospital")
}

data class PhraseItem(
    val id: String,
    val category: PhraseCategory,
    val english: String,
    val turkish: String,
    val turkishPhonetic: String,
    val arabic: String,
    val urdu: String
) {
    fun getTextForLanguage(language: AppLanguage): String {
        return when (language) {
            AppLanguage.ENGLISH -> english
            AppLanguage.TURKISH -> turkish
            AppLanguage.ARABIC -> arabic
            AppLanguage.URDU -> urdu
        }
    }
}
