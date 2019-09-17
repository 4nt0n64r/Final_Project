package com.example.pubgstats

interface StatList {

}

data class StatHeader(
    val text:String
):StatList{}

class StatPoints(
    val text:String,
    val points:String
):StatList{}
