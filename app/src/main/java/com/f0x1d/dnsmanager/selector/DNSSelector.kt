package com.f0x1d.dnsmanager.selector

import android.content.Context
import android.provider.Settings
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.model.DNSMode
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

    private val listeners = mutableListOf<OnDNSSelectedListener>()

    val currentMode: DNSMode get() = ctx.contentResolver.let {
        val mode = Settings.Global.getString(it, PRIVATE_DNS_MODE)
            ?.ifEmpty { null }
            ?: Settings.Global.getString(it, PRIVATE_DNS_DEFAULT_MODE)

        when (mode) {
            PRIVATE_DNS_MODE_OFF -> DNSMode.OFF
            PRIVATE_DNS_MODE_OPPORTUNISTIC -> DNSMode.AUTO
            else -> DNSMode.CUSTOM
        }
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

    fun select(dnsItem: DNSItem) = select(dnsItem.host)

    fun select(host: String) = ctx.contentResolver.let {
        Settings.Global.putString(it, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_PROVIDER_HOSTNAME)
        Settings.Global.putString(it, PRIVATE_DNS_SPECIFIER, host)

        notifyAboutChange(host)
    }

    fun resetSwitch() = when (currentMode) {
        DNSMode.AUTO -> resetOff()
        else -> resetAuto()
    }

    fun resetAuto() = reset(PRIVATE_DNS_MODE_OPPORTUNISTIC)
    fun resetOff() = reset(PRIVATE_DNS_MODE_OFF)

    private fun reset(mode: String) = ctx.contentResolver.let {
        Settings.Global.putString(it, PRIVATE_DNS_MODE, mode)
        Settings.Global.putString(it, PRIVATE_DNS_SPECIFIER, "")

        notifyAboutChange(null)
    }

    fun registerListener(listener: OnDNSSelectedListener) = listeners.add(listener)
    fun unregisterListener(listener: OnDNSSelectedListener) = listeners.remove(listener)

    private fun notifyAboutChange(host: String?) = listeners.forEach {
        it.onSelected(host)
    }

    fun interface OnDNSSelectedListener {
        fun onSelected(host: String?)
    }
}