package com.autumn.leaves

import com.orhanobut.hawk.Hawk
import kotlin.reflect.KProperty

/**
 * Date：2024/6/28
 * Describe:
 */
class AppleCache(private val defApple: String = "", val nameP: String = "Apple_") {
    private val listUseCache = arrayListOf("RefStr", "A_id", "Crisp_", "autumn_length","T_Id")
    private var isUseCache = listUseCache.contains(nameP)
    private var cache = ""

    operator fun getValue(me: Any?, p: KProperty<*>): String {
        if (isUseCache) {
            if (cache.isBlank() || cache == defApple) {
                cache = Hawk.get("$nameP${p.name}", defApple)
            }
            return cache
        }

        return Hawk.get("$nameP${p.name}", defApple)
    }

    operator fun setValue(me: Any?, p: KProperty<*>, value: String) {
        if (isUseCache) {
            cache = value
        }
        Hawk.put("$nameP${p.name}", value)
    }
}