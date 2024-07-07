package com.f0x1d.dnsmanager.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.f0x1d.dnsmanager.database.entity.DNSItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DNSItemDao {

    @Query("SELECT * FROM DNSItem")
    fun getAll(): Flow<List<DNSItem>>

    @Query("SELECT * FROM DNSItem WHERE id = :id")
    fun getById(id: Long): Flow<DNSItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dnsItem: DNSItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dnsItems: List<DNSItem>)

    @Delete
    suspend fun delete(dnsItem: DNSItem)
}
