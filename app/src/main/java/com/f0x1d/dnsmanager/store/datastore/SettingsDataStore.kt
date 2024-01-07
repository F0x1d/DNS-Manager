package com.f0x1d.dnsmanager.store.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.store.datastore.base.BasePreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext context: Context
): BasePreferencesDataStore(context, DATA_STORE_NAME) {

    companion object {
        const val DATA_STORE_NAME = "settings_data"

        val SKIP_AUTO_MODE_KEY = booleanPreferencesKey("skip_auto_mode")
        val LAST_DNS_ITEM_KEY = stringPreferencesKey("last_dns_item")
    }

    val skipAutoMode = getAsFlow(SKIP_AUTO_MODE_KEY).map {
        it ?: false
    }

    val lastDNSItem = getAsFlow(LAST_DNS_ITEM_KEY).map {
        it?.let { Json.decodeFromString<DNSItem>(it) }
    }

    suspend fun saveLastDNSItem(dnsItem: DNSItem?) = save(
        LAST_DNS_ITEM_KEY,
        dnsItem?.let { Json.encodeToString(it) }
    )

    suspend fun saveSkipAutoMode(skip: Boolean?) = save(SKIP_AUTO_MODE_KEY, skip)
}