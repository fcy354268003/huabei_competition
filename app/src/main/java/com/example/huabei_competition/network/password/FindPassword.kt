package com.example.huabei_competition.network.bean

data class FindPasswordFirstReq(val username: String)
data class FindPasswordFirstRep(val phone: String, val token: String)
data class FindPasswordSecondReq(val token: String)
data class FindPasswordThirdReq(val token: String, val verification: String)
data class FindPasswordFourthReq(val token: String, val password: String)
