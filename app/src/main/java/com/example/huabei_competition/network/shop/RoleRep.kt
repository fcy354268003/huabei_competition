package com.example.huabei_competition.network.shop

import com.example.huabei_competition.db.Prop
import com.example.huabei_competition.db.ShopRole

data class RoleRep(val count: String, var info: List<ShopRole>)
data class PropRep(val count: String, var info: List<Prop>)