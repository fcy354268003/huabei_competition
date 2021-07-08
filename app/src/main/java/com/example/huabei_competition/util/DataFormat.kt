package com.example.huabei_competition.util

import android.text.TextUtils
import com.example.huabei_competition.widget.MyToast

class DataFormat {
    companion object {
        @JvmStatic
        fun isStandard(list: List<String>):Boolean {
            for (s in list) {
                if (TextUtils.isEmpty(s)) {
                    MyToast.showMessage("请将表单填写完整")
                    return false
                }
            }
            if (!TextUtils.equals(list.get(2), list.get(3))) {
                MyToast.showMessage("两次填写密码不一致")
                return false
            }
            if (list.get(1).length < 8 || list.get(1).length > 16) {
                MyToast.showMessage("账号填写不符合规则")
                return false
            }
            if (list.get(2).length < 8 || list.get(2).length > 16) {
                MyToast.showMessage("密码填写不符合规则")
                return false
            }

            if (list.get(0).length >= 12) {
                MyToast.showMessage("昵称填写不符合规则")
                return false
            }
            return true
        }
    }
}