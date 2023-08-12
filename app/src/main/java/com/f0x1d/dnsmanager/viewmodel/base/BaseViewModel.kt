package com.f0x1d.dnsmanager.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {
    val ctx get() = getApplication<Application>()
}