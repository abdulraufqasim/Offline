package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.ModelDownloadStatus
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.OfflineBanner
import com.example.ui.components.TranslationCard
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun TranslateScreen(
    sourceLanguage: AppLanguage,
    targetLanguage: AppLanguage,
    sourceText: String,
    translatedText: String,
    isTranslating: Boolean,
    translationError: String?,
    detectedLanguage: AppLanguage?,
    isCurrentFavorite: Boolean,
    downloadStates: Map<AppLanguage, ModelDownloadStatus>,
    isSpeaking: Boolean,
    onSourceTextChanged: (String) -> Unit,
    onTranslateNow: () -> Unit,
    onSwapLanguages: () -> Unit,
    onSetSourceLanguage: (AppLanguage) -> Unit,
    onSetTargetLanguage: (AppLanguage) -> Unit,
    onClearSourceText: () -> Unit,
    onApplyDetectedLanguage: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSpeak: (String, AppLanguage) -> Unit,
    onDownloadLanguage: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }

    var swapRotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = swapRotation,
        animationSpec = tween(durationMillis = 300),
        label = "swapRotation"
    )

    val isSourceDownloaded = downloadStates[sourceLanguage] is ModelDownloadStatus.Downloaded
    val isTargetDownloaded = downloadStates[targetLanguage] is ModelDownloadStatus.Downloaded
    val canTranslateOffline = isSourceDownloaded && isTargetDownloaded

    val sourceDirection = if (sourceLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Language Selector Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language Selector
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { showSourcePicker = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .testTag("source_language_selector"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = sourceLanguage.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = sourceLanguage.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isSourceDownloaded) "Offline ready" else "Pack needed",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSourceDownloaded) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Swap Button
                IconButton(
                    onClick = {
                        swapRotation += 180f
                        onSwapLanguages()
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .rotate(animatedRotation)
                        .testTag("swap_languages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap languages",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Target Language Selector
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { showTargetPicker = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .testTag("target_language_selector"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = targetLanguage.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = targetLanguage.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isTargetDownloaded) "Offline ready" else "Pack needed",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isTargetDownloaded) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Offline Banner (Warns if missing source or target pack)
        OfflineBanner(
            sourceLang = sourceLanguage,
            targetLang = targetLanguage,
            downloadStates = downloadStates,
            onDownloadLanguage = onDownloadLanguage
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("translate_input_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Detected Language suggestion if different from current source
                if (detectedLanguage != null && detectedLanguage != sourceLanguage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .clickable { onApplyDetectedLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("detected_language_chip"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detected ${detectedLanguage.displayName} (${detectedLanguage.flag}) — Tap to switch source",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Text Input Area with RTL support
                CompositionLocalProvider(LocalLayoutDirection provides sourceDirection) {
                    OutlinedTextField(
                        value = sourceText,
                        onValueChange = onSourceTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("source_text_input"),
                        placeholder = {
                            Text(
                                text = "Enter text to translate (${sourceLanguage.displayName})...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = if (sourceLanguage.isRtl) TextAlign.Right else TextAlign.Left
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = if (sourceLanguage.isRtl) TextAlign.Right else TextAlign.Left,
                            lineHeight = if (sourceLanguage.isRtl) 30.sp else 24.sp
                        ),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            onTranslateNow()
                        })
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom toolbar of Input Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Clear button
                        if (sourceText.isNotBlank()) {
                            IconButton(
                                onClick = onClearSourceText,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("clear_text_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Paste button
                        IconButton(
                            onClick = {
                                val clip = clipboardManager.getText()
                                if (clip != null && clip.text.isNotBlank()) {
                                    onSourceTextChanged(clip.text)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("paste_text_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Listen to source text
                        if (sourceText.isNotBlank()) {
                            IconButton(
                                onClick = { onSpeak(sourceText, sourceLanguage) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("speak_source_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OfflinePin,
                                    contentDescription = "Pronounce source text",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${sourceText.length} chars",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Translate Action Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onTranslateNow()
                            },
                            enabled = sourceText.isNotBlank() && !isTranslating,
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("translate_action_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Translating...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Translate")
                            }
                        }
                    }
                }
            }
        }

        // Translation Error Banner
        AnimatedVisibility(
            visible = translationError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val error = translationError ?: ""
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("translation_error_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StatusError.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusError,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Translated Result Card
        Crossfade(
            targetState = translatedText.isNotBlank(),
            animationSpec = tween(300),
            label = "resultCrossfade"
        ) { hasResult ->
            if (hasResult) {
                TranslationCard(
                    translatedText = translatedText,
                    targetLanguage = targetLanguage,
                    isFavorite = isCurrentFavorite,
                    isSpeaking = isSpeaking,
                    onToggleFavorite = onToggleFavorite,
                    onSpeak = { onSpeak(translatedText, targetLanguage) }
                )
            } else if (sourceText.isBlank()) {
                // Quick Traveler Phrase Starters
                QuickStartersCard(
                    onSelect = { starter ->
                        onSourceTextChanged(starter)
                    }
                )
            }
        }
    }

    if (showSourcePicker) {
        LanguageSelectorDialog(
            title = "Select Source Language",
            selectedLanguage = sourceLanguage,
            downloadStates = downloadStates,
            onSelectLanguage = onSetSourceLanguage,
            onDismissRequest = { showSourcePicker = false }
        )
    }

    if (showTargetPicker) {
        LanguageSelectorDialog(
            title = "Select Target Language",
            selectedLanguage = targetLanguage,
            downloadStates = downloadStates,
            onSelectLanguage = onSetTargetLanguage,
            onDismissRequest = { showTargetPicker = false }
        )
    }
}

@Composable
private fun QuickStartersCard(
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Turkey Traveler Starters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            val starters = listOf(
                "Hello, how are you?",
                "How much does this cost?",
                "Where is the nearest taxi?",
                "Can you help me please?",
                "Thank you very much"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    starters.take(3).forEach { phrase ->
                        FilterChip(
                            selected = false,
                            onClick = { onSelect(phrase) },
                            label = { Text(phrase, style = MaterialTheme.typography.bodySmall) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    starters.drop(3).forEach { phrase ->
                        FilterChip(
                            selected = false,
                            onClick = { onSelect(phrase) },
                            label = { Text(phrase, style = MaterialTheme.typography.bodySmall) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}
