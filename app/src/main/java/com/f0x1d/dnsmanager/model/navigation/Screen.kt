package com.f0x1d.dnsmanager.model.navigation

sealed class Screen(val route: String) {
    data object DNSList: Screen("List")
    data object CreateDNSItem: Screen("Create")
}