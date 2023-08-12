package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.f0x1d.dnsmanager.database.AppDatabase
import com.f0x1d.dnsmanager.database.entity.DNSItem
import com.f0x1d.dnsmanager.selector.DNSSelector
import com.f0x1d.dnsmanager.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateDNSItemViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val database: AppDatabase,
    private val selector: DNSSelector,
    application: Application
): BaseViewModel(application) {

    companion object {
        fun provideFactory(
            assistedFactory: CreateDNSItemViewModelFactory,
            id: Long
        ) = viewModelFactory {
            initializer {
                assistedFactory.create(id)
            }
        }
    }

    val dnsItem = database.dnsItems().getById(id)
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

@AssistedFactory
interface CreateDNSItemViewModelFactory {
    fun create(id: Long): CreateDNSItemViewModel
}