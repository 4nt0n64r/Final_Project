package com.example.final_project.database.players

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simple_players")
data class SimplePlayer(
    @ColumnInfo(name = "name_field") val name: String,
    @PrimaryKey
    @ColumnInfo(name = "id_field") val id: String
){
    private fun sPtoSPUI(sp:SimplePlayer):SimplePlayerUI{
        return SimplePlayerUI(sp.name,sp.id,false)
    }
}

data class SimplePlayerUI(
    val name: String,
    val id: String,
    val isSelected: Boolean = false
){
    private fun sPUItoSP(sp:SimplePlayerUI):SimplePlayer{
        return SimplePlayer(sp.name,sp.id)
    }
}
