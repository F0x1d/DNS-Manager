package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DNSListViewModel @Inject constructor(
    private val database: AppDatabase,
    private val selector: DNSSelector,
    private val settingsDataStore: SettingsDataStore,
    application: Application
): BaseViewModel(application), DNSSelector.OnDNSSelectedListener {

    val dnsItems = database.dnsItems().getAll()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val currentDNSHost = MutableStateFlow<String?>(null)

    init {
        selector.registerListener(this)
    }

    fun select(dnsItem: DNSItem) = selector.select(dnsItem).also {
        updateSelectedDNSHost()

        viewModelScope.launch {
            settingsDataStore.saveLastHost(dnsItem.host)
        }
    }

    fun delete(dnsItem: DNSItem) = viewModelScope.launch(Dispatchers.IO) {
        if (dnsItem.host == currentDNSHost.value) selector.reset()

        database.dnsItems().delete(dnsItem)
    }

    fun reset() = selector.reset().also {
        updateSelectedDNSHost()
    }

    fun updateSelectedDNSHost() = currentDNSHost.update {
        selector.currentHost
    }

    override fun onCleared() {
        selector.unregisterListener(this)
    }

    override fun onSelected(host: String?) {
        currentDNSHost.update { host }
    }
}