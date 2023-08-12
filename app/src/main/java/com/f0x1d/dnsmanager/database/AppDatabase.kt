package com.f0x1d.dnsmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.f0x1d.dnsmanager.database.dao.DNSItemDao
import com.f0x1d.dnsmanager.database.entity.DNSItem

@Database(entities = [DNSItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dnsItems(): DNSItemDao
}