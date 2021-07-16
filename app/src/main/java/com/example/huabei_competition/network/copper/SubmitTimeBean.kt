package com.example.huabei_competition.network.copper

import com.example.huabei_competition.event.UserUtil

data class SubmitTimeBean(val time: String, val label: String,val token: String = UserUtil.sUserName) {
    private val sign: String = EncryptionTransmission.encode(token + time)

}
