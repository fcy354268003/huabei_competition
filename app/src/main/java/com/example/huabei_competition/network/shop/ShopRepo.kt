package com.example.huabei_competition.network.shop

import androidx.lifecycle.MutableLiveData
import com.example.huabei_competition.network.ServiceManager
import com.example.huabei_competition.repository.LocalDao

object ShopRepo {
    private val service: ShopService = ServiceManager.getService<ShopService>()
    suspend fun getRole(liveData: MutableLiveData<RoleRep>) {
        val response = service.getRoleList()
        val roleRep = if (response.code != "0000") {
            // 读缓存
            LocalDao.getShopRoleCache()
        } else {
            LocalDao.setShopRoleCache(response.data!!)
            response.data
        }
        liveData.postValue(roleRep)
    }

    suspend fun getProp(liveData: MutableLiveData<PropRep>) {
        val response = service.getPropList()
        val propRep = if (response.code != "0000") {
            // 读缓存
            LocalDao.getShopPropCache()
        } else {
            LocalDao.setShopPropCache(response.data!!)
            response.data
        }
        liveData.postValue(propRep)
    }

    suspend fun buyProp(liveData: MutableLiveData<Boolean>, buyBean: BuyBean) {
        val buyProps = service.buyProps(buyBean)
        if (buyProps.code == "0000") {
            liveData.postValue(true)
        } else {
            liveData.postValue(false)
        }
    }

    suspend fun buyRole(liveData: MutableLiveData<Boolean>, buyBean: BuyBean) {
        val buyProps = service.butRole(buyBean)
        if (buyProps.code == "0000") {
            liveData.postValue(true)
        } else {
            liveData.postValue(false)
        }
    }
}