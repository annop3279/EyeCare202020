package com.ankn.core.domain.util

interface ResourceProvider {
    fun getString(resId: Int): String
}