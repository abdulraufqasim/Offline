package com.example.data.model

import com.google.mlkit.nl.translate.TranslateLanguage

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val isRtl: Boolean,
    val mlKitCode: String,
    val approxSizeMb: Int = 30
) {
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeName = "English",
        flag = "🇬🇧",
        isRtl = false,
        mlKitCode = TranslateLanguage.ENGLISH,
        approxSizeMb = 30
    ),
    TURKISH(
        code = "tr",
        displayName = "Turkish",
        nativeName = "Türkçe",
        flag = "🇹🇷",
        isRtl = false,
        mlKitCode = TranslateLanguage.TURKISH,
        approxSizeMb = 30
    ),
    ARABIC(
        code = "ar",
        displayName = "Arabic",
        nativeName = "العربية",
        flag = "🇸🇦",
        isRtl = true,
        mlKitCode = TranslateLanguage.ARABIC,
        approxSizeMb = 30
    ),
    URDU(
        code = "ur",
        displayName = "Urdu",
        nativeName = "اردو",
        flag = "🇵🇰",
        isRtl = true,
        mlKitCode = TranslateLanguage.URDU,
        approxSizeMb = 30
    );

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }

        fun fromMlKitCode(mlKitCode: String): AppLanguage? {
            return entries.find { it.mlKitCode.equals(mlKitCode, ignoreCase = true) }
        }
    }
}
