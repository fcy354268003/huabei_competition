package com.example.huabei_competition.network.shop

import com.example.huabei_competition.network.ApiResponse
import com.example.huabei_competition.network.copper.CopperRep
import retrofit2.http.Body
import retrofit2.http.POST

interface ShopService {
    //获取道具列表
    @POST("/story/getPropList")
    suspend fun getPropList(): ApiResponse<PropRep>

    //获取角色列表
    @POST("/story/getRoleList")
    suspend fun getRoleList(): ApiResponse<RoleRep>

    //购买物品
    @POST("/story/buyProps")
    suspend fun buyProps(@Body buyBean: BuyBean): ApiResponse<CopperRep>


    //购买角色
    @POST("/story/buyRoles")
    suspend fun butRole(@Body buyBean: BuyBean):ApiResponse<CopperRep>


}