package com.example.huabei_competition.repository

import android.content.Context
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.example.huabei_competition.db.Prop
import com.example.huabei_competition.db.ShopRole
import com.example.huabei_competition.event.UserUtil
import com.example.huabei_competition.network.shop.PropRep
import com.example.huabei_competition.network.shop.RoleRep
import com.example.huabei_competition.util.MyApplication
import com.example.huabei_competition.util.toast
import com.google.gson.Gson

/**
 * 保存缓存数据，当从远程拉取数据失败的时候，直接解析缓存在本地的json数据，如果远程代码库拉取成功则，json序列化数据保存缓存
 * 现阶段缓存数据量不多，先使用SP
 *
 * 1.用户信息，包括头像缓存到本地
 * 2.商店相关，不缓存商店照片
 * 3.学习数据展示界面的数据
 * 4.排行榜相关信息，不缓存头像
 * 5.用户重复登陆相关
 * 所有不缓存头像的只缓存头像的URL
 */
object LocalDao {
    private val defString = ""
    private val mGson by lazy { Gson() }
    private val KEY_LOG = "login"
    private val KEY_PASS = "pass"
    private val SP_userInfo = "userInfo"
    private val SP_CACHE = "cache${UserUtil.sUserName}"
    private val KEY_MINE = "mine"
    private val KEY_SHOP_ROLE = "role"
    private val KEY_SHOP_PROP = "prop"
    private fun getSP(name: String) = MyApplication.getApplicationByReflect().getSharedPreferences(name, Context.MODE_PRIVATE)

    /**
     * 获取用户信息的本地缓存，可以将用户的头像也试着缓存到本地
     */
    fun getMineCache(userName: String = UserUtil.sUserName): UserInfo? {
        val mineCache = getSP(SP_CACHE).getString(KEY_MINE, defString)
        if (mineCache == defString)
            return null
        return mGson.fromJson(mineCache, UserInfo::class.java)
    }

    /**
     * 拿到商店角色缓存 不将图片缓存到本地
     * @return List<ShopRole>
     */
    fun getShopRoleCache(userName: String = UserUtil.sUserName): RoleRep? {
        val content = getSP(SP_CACHE).getString(KEY_SHOP_ROLE, defString)
        if (content == defString) {
            return null
        }
        return mGson.fromJson(content, List::class.java) as RoleRep
    }

    /**
     *
     */
    fun getShopPropCache(): PropRep? {
        val content = getSP(SP_CACHE).getString(KEY_SHOP_PROP, defString)
        if (content == defString) {
            return null
        }
        return mGson.fromJson(content, List::class.java) as PropRep
    }


    fun setShopPropCache(content: PropRep) {
        val edit = getSP(SP_CACHE).edit()
        edit.putString(KEY_SHOP_PROP, mGson.toJson(content))
        edit.apply()
    }

    fun setShopRoleCache(content: RoleRep) {
        val edit = getSP(SP_CACHE).edit()
        edit.putString(KEY_SHOP_ROLE, mGson.toJson(content))
        edit.apply()
    }

    fun setMineCache(userInfo: UserInfo) {
        val edit = getSP(SP_CACHE).edit()
        edit.putString(KEY_MINE, mGson.toJson(userInfo))
        edit.apply()
    }

    /**
     * 缓存各个用户的token,密码账号
     * 直接初始化UserUtil相关信息
     * 通过辨别方法返回后UserUtil是否初始化做判断是否已经登陆
     */
    fun isUserLogin() {
        getSP(SP_userInfo).apply {
            val userName = getString(KEY_LOG, "")
            // 暂时先使用明文存储
            if (userName != null) {
                val pass = getString(KEY_PASS, "")
                // 极光登录
                JMessageClient.login(userName, pass, object : BasicCallback() {
                    override fun gotResult(p0: Int, p1: String?) {
                        if (p0 == 0) {
                            // 初始化UserUtil相关属性
                            UserUtil.sUserName = userName
                        } else {
                            "密码失效请尝试重新登录".toast()
                        }
                    }

                })
            }
        }
    }
}