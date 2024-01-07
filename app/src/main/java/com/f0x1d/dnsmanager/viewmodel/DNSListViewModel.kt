package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun select(dnsItem: DNSItem) = viewModelScope.launch {
        if (currentDNSHost.value == dnsItem.host)
            reset().join()
        else
            selector.select(dnsItem)

        updateSelectedDNSHost()
        settingsDataStore.saveLastDNSItem(dnsItem)
    }

    fun delete(dnsItem: DNSItem) = viewModelScope.launch {
        if (dnsItem.host == currentDNSHost.value) selector.resetSwitch()

        if (dnsItem.id == settingsDataStore.lastDNSItem.first()?.id)
            settingsDataStore.saveLastDNSItem(null)

        withContext(Dispatchers.IO) {
            database.dnsItems().delete(dnsItem)
        }
    }

    fun reset() = viewModelScope.launch {
        selector.resetSwitch()

        updateSelectedDNSHost()
        Toast.makeText(
            ctx,
            ctx.getString(
                R.string.switched_to,
                ctx.getString(selector.currentMode.title)
            ),
            Toast.LENGTH_SHORT
        ).show()
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