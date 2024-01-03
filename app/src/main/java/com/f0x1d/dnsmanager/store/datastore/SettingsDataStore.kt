package com.f0x1d.dnsmanager.store.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.f0x1d.dnsmanager.store.datastore.base.BaseDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext context: Context
): BaseDataStore(context, DATA_STORE_NAME) {

    companion object {
        const val DATA_STORE_NAME = "settings_data"

        val LAST_HOST_KEY = stringPreferencesKey("last_host")
    }

    val lastHost = getAsFlow(LAST_HOST_KEY)

    suspend fun saveLastHost(host: String?) = save(LAST_HOST_KEY, host)
}