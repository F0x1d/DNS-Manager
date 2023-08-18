package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.di.viewmodel.DNSItemId
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateDNSItemViewModel @Inject constructor(
    @DNSItemId val id: Long?,
    private val database: AppDatabase,
    private val selector: DNSSelector,
    application: Application
): BaseViewModel(application) {

    val dnsItem = database.dnsItems().getById(id ?: -1L)
        .filterNotNull()
        .flowOn(Dispatchers.IO)

    fun create(
        item: DNSItem?,
        name: String,
        host: String,
        onDone: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val newItem = item?.copy(name = name, host = host) ?: DNSItem(name, host)
        database.dnsItems().insert(newItem)

        if (item != null && selector.currentHost == item.host) selector.select(
            newItem
        )

        withContext(Dispatchers.Main) { onDone() }
    }
}