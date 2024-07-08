package com.amroid.fetcher.domain.entities

data class Param(var key: String = "", var value: String = "", val type: ParamType, var fileUri:String ="",
                 var isFile:Boolean = false)