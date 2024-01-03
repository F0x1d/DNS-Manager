package com.f0x1d.dnsmanager.service.qs

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SwitchModeTileService: TileService() {

    @Inject
    lateinit var selector: DNSSelector

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onStartListening() {
        super.onStartListening()

        val lastHost = runBlocking {
            settingsDataStore.lastHost.first()
        }

        if (lastHost == null) {
            val tile = qsTile
            tile.state = Tile.STATE_UNAVAILABLE
            tile.updateTile()
        } else
            updateTile()
    }

    override fun onClick() {
        super.onClick()

        val lastHost = runBlocking {
            settingsDataStore.lastHost.first()
        } ?: return

        if (selector.currentHost == null)
            selector.select(lastHost)
        else
            selector.reset()

        updateTile()
    }

    private fun updateTile() {
        val currentHost = selector.currentHost

        val tile = qsTile
        val subtitle = when (currentHost) {
            null -> getString(R.string.auto_mode)

            else -> currentHost
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            tile.subtitle = subtitle
        else
            tile.label = subtitle

        tile.state = when (currentHost) {
            null -> Tile.STATE_INACTIVE

            else -> Tile.STATE_ACTIVE
        }

        tile.updateTile()
    }
}