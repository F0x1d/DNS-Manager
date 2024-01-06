package com.f0x1d.dnsmanager.model

import androidx.annotation.StringRes
import com.f0x1d.dnsmanager.R

enum class DNSMode {
    OFF, AUTO, CUSTOM;

    @get:StringRes val title get() = when (this) {
        OFF -> androidx.compose.ui.R.string.off
        AUTO -> R.string.auto
        CUSTOM -> R.string.custom
    }

    fun next() = when (this) {
        OFF -> AUTO
        AUTO -> CUSTOM
        CUSTOM -> OFF
    }
}