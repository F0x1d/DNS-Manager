package com.f0x1d.dnsmanager.service.qs.base

import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseTileService: TileService() {

    protected val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    protected var listeningScope: CoroutineScope? = null

    protected var canUpdate = false

    override fun onStartListening() {
        super.onStartListening()
        listeningScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        canUpdate = true
    }

    override fun onStopListening() {
        super.onStopListening()
        listeningScope?.cancel()
        canUpdate = false
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}