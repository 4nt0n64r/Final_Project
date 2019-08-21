package com.example.final_project

interface StatList {

}

data class StatHeader(
    val text:String
):StatList{}

class StatPoints(
    val text:String,
    val points:String
):StatList{}
