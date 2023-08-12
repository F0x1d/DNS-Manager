package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.selector.DNSSelector
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
    application: Application
): BaseViewModel(application) {

    val dnsItems = database.dnsItems().getAll()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val currentDNSHost = MutableStateFlow<String?>(null)

    fun select(dnsItem: DNSItem) = selector.select(dnsItem).also {
        updateSelectedDNSHost()
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
}