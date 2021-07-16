package com.example.huabei_competition.network.copper

import com.example.huabei_competition.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CopperService {
    @POST("/postPersonTime")
    suspend fun submitPersonTime(@Body submitTimeBean: SubmitTimeBean): ApiResponse<Unit>

    @POST("/postTeamTime")
    suspend fun submitGroupTime(@Body submitTimeBean: SubmitTimeBean): ApiResponse<Unit>

    @POST("/queryMoney")
    suspend fun query(@Body queryBean: QueryBean): ApiResponse<CopperRep>
}