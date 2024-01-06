package com.f0x1d.dnsmanager.service.qs

import android.os.Build
import android.service.quicksettings.Tile
import com.f0x1d.dnsmanager.model.DNSMode
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.service.qs.base.BaseTileService
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SwitchModeTileService: BaseTileService() {

    @Inject
    lateinit var selector: DNSSelector

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        scope.launch {
            val lastHost = settingsDataStore.lastHost.first()

            when (selector.currentMode.next()) {
                DNSMode.OFF -> selector.resetOff()
                DNSMode.AUTO -> selector.resetAuto()

                DNSMode.CUSTOM -> {
                    if (lastHost != null)
                        selector.select(lastHost)
                    else
                        selector.resetOff()
                }
            }

            updateTile()
        }
    }

    private fun updateTile() = qsTile.apply {
        val currentMode = selector.currentMode
        val modeStatus = getString(currentMode.title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            subtitle = modeStatus
        else
            label = modeStatus

        state = when (currentMode) {
            DNSMode.OFF, DNSMode.AUTO -> Tile.STATE_INACTIVE

            else -> Tile.STATE_ACTIVE
        }
    }.updateTile()
}