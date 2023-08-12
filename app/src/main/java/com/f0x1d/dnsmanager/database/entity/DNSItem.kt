package com.f0x1d.dnsmanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["host"], unique = true)])
data class DNSItem(
    @ColumnInfo("name") val name: String,
    @ColumnInfo("host") val host: String,
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)