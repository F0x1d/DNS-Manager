package com.f0x1d.dnsmanager.service.qs

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.model.DNSMode
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.service.qs.base.BaseTileService
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

        settingsDataStore.lastDNSItem
            .onEach(this::updateTile)
            .launchIn(listeningScope ?: return)
    }

    override fun onClick() {
        super.onClick()

        scope.launch {
            val lastDNSItem = settingsDataStore.lastDNSItem.first()

            when (selector.currentMode.next()) {
                DNSMode.OFF -> selector.resetOff()
                DNSMode.AUTO -> selector.resetAuto()

                DNSMode.CUSTOM -> {
                    if (lastDNSItem != null)
                        selector.select(lastDNSItem)
                    else
                        selector.resetOff()
                }
            }

            updateTile(lastDNSItem)
        }
    }

    private fun updateTile(dnsItem: DNSItem?) = qsTile.apply {
        val currentMode = selector.currentMode
        val modeStatus = when (currentMode) {
            DNSMode.CUSTOM -> dnsItem?.name

            else -> getString(currentMode.title)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            subtitle = modeStatus
        else
            label = modeStatus

        state = when (currentMode) {
            DNSMode.OFF, DNSMode.AUTO -> Tile.STATE_INACTIVE

            else -> Tile.STATE_ACTIVE
        }

        icon = Icon.createWithResource(
            this@SwitchModeTileService,
            when (state) {
                Tile.STATE_ACTIVE -> R.drawable.ic_switch_on

                else -> R.drawable.ic_switch_off
            }
        )
    }.updateTile()
}