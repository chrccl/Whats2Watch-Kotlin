package it.namenotfoundexception.whats2watch.model

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes id: Int, vararg args: Any?): String
}