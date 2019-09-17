package com.example.pubgstats.database.seasons

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class SeasonsData(
    @SerializedName("data")
    val seasons: List<Season>
)

data class Season(
    val id: String,
    @SerializedName("attributes")
    val attributes: SeasonAttributes
    //val nameOfSeason:String
) {
    override fun toString(): String {
        return id
    }

    private fun toSeasonsUI(): SeasonUI {
        return SeasonUI(id)
    }

    private fun toSeasonsDB(): SeasonDB {
        return SeasonDB(id)
    }
}

data class SeasonAttributes(
    val isCurrentSeason: String,
    val isOffseason: String
)

data class SeasonUI(
    val seasonId: String,
    val seasonName: String = "SEASON_NAME"
) {
    private fun toSeasonsDB(): SeasonDB {
        return SeasonDB(seasonId)
    }
}

@Entity(tableName = "seasons")
data class SeasonDB(
    @PrimaryKey
    @ColumnInfo(name = "id_field") val seasonId: String
) {
    private fun toSeasonsUI(): SeasonUI {
        return SeasonUI(seasonId)
    }
}

@Entity(tableName = "download")
data class DownloadDate(
    @PrimaryKey
    @ColumnInfo(name = "date_field") val ddate: Int
)





