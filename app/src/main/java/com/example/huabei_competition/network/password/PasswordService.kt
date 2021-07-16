package com.example.huabei_competition.network.password

import com.example.huabei_competition.network.ApiResponse
import com.example.huabei_competition.network.bean.*
import retrofit2.http.Body
import retrofit2.http.POST

interface PasswordService {
    /**
     * 修改密码
     */
    @POST("/changePassword")
    suspend fun changePassword(@Body con: PasswordChangeBean): ApiResponse<Unit>


    @POST("/forget/getUser")
    suspend fun findPassFirst(@Body con: FindPasswordFirstReq): ApiResponse<FindPasswordFirstRep>

    @POST("/forget/verification")
    suspend fun findPassSecond(@Body con: FindPasswordSecondReq): ApiResponse<Unit>

    @POST("/forget/checkVerification")
    suspend fun findPasswordThird(@Body con: FindPasswordThirdReq): ApiResponse<FindPasswordSecondReq>

    @POST("/forget/changePassword")
    suspend fun findPasswordFourth(@Body con: FindPasswordFourthReq): ApiResponse<Unit>

}