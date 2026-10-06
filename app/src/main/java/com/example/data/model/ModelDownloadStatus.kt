package com.example.data.model

sealed interface ModelDownloadStatus {
    data object NotDownloaded : ModelDownloadStatus
    data class Downloading(val progressPercent: Int? = null) : ModelDownloadStatus
    data object Downloaded : ModelDownloadStatus
    data class Error(val errorMessage: String) : ModelDownloadStatus
}
