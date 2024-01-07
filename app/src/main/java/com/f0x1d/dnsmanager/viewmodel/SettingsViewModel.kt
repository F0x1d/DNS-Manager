package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsDataStore: SettingsDataStore,
    application: Application
): BaseViewModel(application)