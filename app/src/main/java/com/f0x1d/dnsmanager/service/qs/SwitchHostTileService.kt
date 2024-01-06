package com.f0x1d.dnsmanager.service.qs

import android.os.Build
import android.service.quicksettings.Tile
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.model.DNSMode
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.service.qs.base.BaseTileService
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SwitchHostTileService: BaseTileService() {

    @Inject
    lateinit var selector: DNSSelector

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onStartListening() {
        super.onStartListening()

        scope.launch {
            val lastHost = settingsDataStore.lastHost.first()
            if (!canUpdate) return@launch

            updateTile(lastHost)
        }
    }

    override fun onClick() {
        super.onClick()

        scope.launch {
            val lastHost = settingsDataStore.lastHost.first()

            val dnsItems = database.dnsItems().getAll().flowOn(Dispatchers.IO).first()
            val nextIndex = dnsItems.indexOfLast { it.host == lastHost } + 1

            val newDNSItem = dnsItems.getOrNull(nextIndex) ?: dnsItems.firstOrNull() ?: return@launch
            settingsDataStore.saveLastHost(newDNSItem.host)

            if (selector.currentMode == DNSMode.CUSTOM)
                selector.select(newDNSItem)

            updateTile(newDNSItem.host)
        }
    }

    private fun updateTile(host: String?) = qsTile.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            subtitle = host
        else
            label = host

        state = when (host != null) {
            true -> Tile.STATE_ACTIVE

            else -> Tile.STATE_INACTIVE
        }
    }.updateTile()
}