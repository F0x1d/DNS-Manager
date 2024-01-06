package com.f0x1d.dnsmanager.service.qs.base

import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseTileService: TileService() {

    private val job = SupervisorJob()
    protected val scope = CoroutineScope(Dispatchers.Main + job)

    protected var canUpdate = false

    override fun onStartListening() {
        super.onStartListening()
        canUpdate = true
    }

    override fun onStopListening() {
        super.onStopListening()
        canUpdate = false
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}