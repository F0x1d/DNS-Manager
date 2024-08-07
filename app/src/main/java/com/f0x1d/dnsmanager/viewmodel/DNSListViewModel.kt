package com.f0x1d.dnsmanager.viewmodel

import android.app.Application
import android.net.Uri
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
        .onEach { updateSelectedDNSHost() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

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

    fun import(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                database.dnsItems().insert(
                    Json.decodeFromString<List<DNSItem>>(
                        inputStream.bufferedReader().readText(),
                    ),
                )
            }
        }
    }

    fun export(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(Json.encodeToString(dnsItems.value).encodeToByteArray())
                outputStream.flush()
            }
        }
    }

    override fun onCleared() {
        selector.unregisterListener(this)
    }

    override fun onSelected(host: String?) {
        currentDNSHost.update { host }
    }
}
