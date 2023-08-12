package com.f0x1d.dnsmanager.viewmodel

import android.Manifest
import android.app.Application
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
): BaseViewModel(application) {
    val permissionGranted = PermissionFlow
        .getInstance()
        .getPermissionState(Manifest.permission.WRITE_SECURE_SETTINGS)
        .map { it.isGranted }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
}