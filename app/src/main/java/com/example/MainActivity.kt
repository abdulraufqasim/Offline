package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.AppLanguage
import com.example.data.model.ModelDownloadStatus
import com.example.data.repository.MlKitTranslationService
import com.example.data.repository.TranslationRepository
import com.example.ui.screens.HistoryAndSavedScreen
import com.example.ui.screens.LanguageManagerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PhrasebookScreen
import com.example.ui.screens.TranslateScreen
import com.example.ui.theme.OfflineTranslateTheme
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel
import com.example.util.TtsManager

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val translationRepo = TranslationRepository(db.translationDao())
        val preferencesRepo = UserPreferencesRepository(applicationContext)
        val translationService = MlKitTranslationService()
        val ttsManager = TtsManager(applicationContext)

        val factory = MainViewModel.Factory(
            translationService = translationService,
            translationRepo = translationRepo,
            preferencesRepo = preferencesRepo,
            ttsManager = ttsManager
        )
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

            OfflineTranslateTheme(themeMode = themeMode) {
                if (!isOnboardingCompleted) {
                    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
                    OnboardingScreen(
                        downloadStates = downloadStates,
                        onDownloadLanguage = { viewModel.downloadModel(it) },
                        onDeleteLanguage = { viewModel.deleteModel(it) },
                        onCompleteOnboarding = { viewModel.completeOnboarding() }
                    )
                } else {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val sourceLang by viewModel.sourceLanguage.collectAsStateWithLifecycle()
    val targetLang by viewModel.targetLanguage.collectAsStateWithLifecycle()
    val sourceText by viewModel.sourceText.collectAsStateWithLifecycle()
    val translatedText by viewModel.translatedText.collectAsStateWithLifecycle()
    val isTranslating by viewModel.isTranslating.collectAsStateWithLifecycle()
    val translationError by viewModel.translationError.collectAsStateWithLifecycle()
    val detectedLanguage by viewModel.detectedLanguage.collectAsStateWithLifecycle()
    val isCurrentFavorite by viewModel.isCurrentFavorite.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsStateWithLifecycle()
    val ttsError by viewModel.ttsManager.lastError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(ttsError) {
        ttsError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.ttsManager.clearError()
        }
    }

    // Handle system back gesture
    if (currentDestination != AppDestination.TRANSLATE) {
        BackHandler {
            viewModel.navigateTo(AppDestination.TRANSLATE)
        }
    }

    val isCurrentPairDownloaded =
        (downloadStates[sourceLang] is ModelDownloadStatus.Downloaded) &&
        (downloadStates[targetLang] is ModelDownloadStatus.Downloaded)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "OfflineTranslate",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCurrentPairDownloaded) StatusSuccess.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrentPairDownloaded) StatusSuccess else MaterialTheme.colorScheme.tertiary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCurrentPairDownloaded) "Offline" else "Pack missing",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrentPairDownloaded) StatusSuccess else MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentDestination == AppDestination.TRANSLATE,
                    onClick = { viewModel.navigateTo(AppDestination.TRANSLATE) },
                    icon = { Icon(Icons.Default.Translate, contentDescription = null) },
                    label = { Text("Translate") },
                    modifier = Modifier.testTag("nav_translate"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.PHRASEBOOK,
                    onClick = { viewModel.navigateTo(AppDestination.PHRASEBOOK) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Phrasebook") },
                    modifier = Modifier.testTag("nav_phrasebook"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.SAVED,
                    onClick = { viewModel.navigateTo(AppDestination.SAVED) },
                    icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = null) },
                    label = { Text("History") },
                    modifier = Modifier.testTag("nav_history"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.SETTINGS,
                    onClick = { viewModel.navigateTo(AppDestination.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Packs") },
                    modifier = Modifier.testTag("nav_settings"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "screenTransition"
        ) { destination ->
            when (destination) {
                AppDestination.TRANSLATE -> {
                    TranslateScreen(
                        sourceLanguage = sourceLang,
                        targetLanguage = targetLang,
                        sourceText = sourceText,
                        translatedText = translatedText,
                        isTranslating = isTranslating,
                        translationError = translationError,
                        detectedLanguage = detectedLanguage,
                        isCurrentFavorite = isCurrentFavorite,
                        downloadStates = downloadStates,
                        isSpeaking = isSpeaking,
                        onSourceTextChanged = { viewModel.onSourceTextChanged(it) },
                        onTranslateNow = { viewModel.translateNow() },
                        onSwapLanguages = { viewModel.swapLanguages() },
                        onSetSourceLanguage = { viewModel.setSourceLanguage(it) },
                        onSetTargetLanguage = { viewModel.setTargetLanguage(it) },
                        onClearSourceText = { viewModel.clearSourceText() },
                        onApplyDetectedLanguage = { viewModel.applyDetectedLanguage() },
                        onToggleFavorite = { viewModel.toggleFavoriteCurrent() },
                        onSpeak = { text, lang -> viewModel.speak(text, lang) },
                        onDownloadLanguage = { viewModel.downloadModel(it) }
                    )
                }

                AppDestination.PHRASEBOOK -> {
                    PhrasebookScreen(
                        onOpenInTranslate = { source, sLang, target, tLang ->
                            viewModel.loadFromHistoryOrPhrasebook(source, sLang, target, tLang)
                        },
                        onSpeak = { text, lang -> viewModel.speak(text, lang) },
                        isSpeaking = isSpeaking
                    )
                }

                AppDestination.SAVED -> {
                    HistoryAndSavedScreen(
                        history = history,
                        favorites = favorites,
                        onSelectTranslation = { item ->
                            val sLang = AppLanguage.fromCode(item.sourceLangCode)
                            val tLang = AppLanguage.fromCode(item.targetLangCode)
                            viewModel.loadFromHistoryOrPhrasebook(item.sourceText, sLang, item.translatedText, tLang)
                        },
                        onToggleFavorite = { item -> viewModel.toggleFavoriteItem(item) },
                        onDeleteItem = { id -> viewModel.deleteHistoryItem(id) },
                        onClearHistory = { keepFav -> viewModel.clearHistory(keepFav) },
                        onSpeak = { text, lang -> viewModel.speak(text, lang) }
                    )
                }

                AppDestination.SETTINGS -> {
                    LanguageManagerScreen(
                        downloadStates = downloadStates,
                        themeMode = themeMode,
                        onDownloadLanguage = { viewModel.downloadModel(it) },
                        onDeleteLanguage = { viewModel.deleteModel(it) },
                        onRefreshStatus = { viewModel.refreshModelStatuses() },
                        onSetThemeMode = { viewModel.setThemeMode(it) }
                    )
                }
            }
        }
    }
}
