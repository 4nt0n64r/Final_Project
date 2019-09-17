package com.example.pubgstats.database.players


import com.google.gson.annotations.SerializedName

interface APIAnswer


data class PlayerData(
    @SerializedName("data")
    //хоть тут и лист, но заказываем мы одного игрока по имени,
    //так приходится делать, т.к. апи подразумевает запрос
    //списка имен, иначе получить id игрока нельзя :(
    //id нужен уже для всех остальных запросов
    val player: List<Player>

) :APIAnswer{

    fun getId(): String {
        return player[0].id
    }

    fun getName(): String {
        return player[0].attributes.name
    }
}

data class Player(
    val id: String,
    @SerializedName("attributes")
    val attributes: PlayerAttributes
)

data class PlayerAttributes(
    val name: String
)

//TODO( вот тут мы можем получить: {"errors":[{"title":"Not Found","detail":"No Players Found Matching Criteria"}]})
//
//data class ErrorsData(
//    @SerializedName("errors")
//val error: List<ErrorInfo>
//) :APIAnswer
//
//data class ErrorInfo(
//    @SerializedName("title")
//    val title: String,
//    @SerializedName("detail")
//    val detail: String
//)
