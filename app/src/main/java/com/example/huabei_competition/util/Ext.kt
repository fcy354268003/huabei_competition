package com.example.huabei_competition.util

import com.example.huabei_competition.widget.MyToast

fun String.toast() {
    MyToast.showMessage(this)
}