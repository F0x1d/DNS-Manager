package com.f0x1d.dnsmanager.selector

import android.content.Context
import android.provider.Settings
import com.f0x1d.dnsmanager.database.entity.DNSItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DNSSelector @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    companion object {
        const val PRIVATE_DNS_DEFAULT_MODE = "private_dns_default_mode"
        const val PRIVATE_DNS_MODE = "private_dns_mode"
        const val PRIVATE_DNS_MODE_OFF = "off"
        const val PRIVATE_DNS_MODE_OPPORTUNISTIC = "opportunistic"
        const val PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = "hostname"
        const val PRIVATE_DNS_SPECIFIER = "private_dns_specifier"
    }

    val currentHost: String? get() = ctx.contentResolver.let {
        val mode = Settings.Global.getString(it, PRIVATE_DNS_MODE)
            ?.ifEmpty { null }
            ?: Settings.Global.getString(it, PRIVATE_DNS_DEFAULT_MODE)

        when (mode) {
            PRIVATE_DNS_MODE_PROVIDER_HOSTNAME -> {
                Settings.Global.getString(it, PRIVATE_DNS_SPECIFIER)
            }

            else -> null
        }
    }

    fun select(dnsItem: DNSItem) = ctx.contentResolver.let {
        Settings.Global.putString(it, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_PROVIDER_HOSTNAME)
        Settings.Global.putString(it, PRIVATE_DNS_SPECIFIER, dnsItem.host)
    }

    fun reset() = ctx.contentResolver.let {
        Settings.Global.putString(it, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_OPPORTUNISTIC)
        Settings.Global.putString(it, PRIVATE_DNS_SPECIFIER, "")
    }
}