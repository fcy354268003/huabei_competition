package com.example.huabei_competition.ui.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huabei_competition.network.shop.PropRep
import com.example.huabei_competition.network.shop.ShopRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopVM : ViewModel() {
    val liveData: MutableLiveData<PropRep> = MutableLiveData()

    fun get() {
        viewModelScope.launch(Dispatchers.IO) {
            ShopRepo.getProp(liveData)
        }
    }
}