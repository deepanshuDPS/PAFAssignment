package com.paf.assignment.utils

enum class PagingState {
    DONE, LOADING, ERROR
}

data class PagingResponseStates(val state: PagingState, val throwable: Throwable? = null)