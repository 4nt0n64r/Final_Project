package com.example.pubgstats.database.players

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players_table")
data class SimplePlayer(
    @ColumnInfo(name = "name_field") val name: String,
    @PrimaryKey
    @ColumnInfo(name = "id_field") val id: String
){
    fun toSPUI(): SimplePlayerUI {
        return SimplePlayerUI(this.name, this.id, false)
    }
}

data class SimplePlayerUI(
    val name: String,
    val id: String,
    var isSelected: Boolean = false
){
    fun toSP(): SimplePlayer {
        return SimplePlayer(this.name, this.id)
    }
}
