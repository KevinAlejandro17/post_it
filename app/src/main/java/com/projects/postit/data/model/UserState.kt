package com.projects.postit.data.model

sealed class UserState {
    object Loading: UserState()
    data class Success(val message: String): UserState()
    data class Error(val message: String): UserState()
}

enum class UserSessionState {
    LOGGED_IN,
    LOGGED_OUT
}