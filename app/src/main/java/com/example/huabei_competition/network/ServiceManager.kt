package com.example.huabei_competition.network

/**
 * 管理各种service
 */
class ServiceManager {
    companion object {
        val map = mutableMapOf<Class<*>, Any>()

        val retrofit = RetrofitFactory.create()

        inline fun <reified T> getService(): T {
            if (!map.contains(T::class.java)) {
                synchronized(ServiceManager::class) {
                    if (!map.containsKey(T::class.java)) {
                        map[T::class.java] = retrofit.create(T::class.java)!!
                    }
                }
            }
            return map[T::class.java] as T
        }

    }

}