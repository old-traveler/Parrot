package com.hyc.parrot

import com.google.gson.Gson
import com.hyc.parrot_lib.JsonConvert

/**
 * @author: 贺宇成
 * @date: 2019-12-13 10:08
 * @desc:
 */
class MyJsonConvert : JsonConvert {
  private val mGson : Gson = Gson()

  override fun <T> fromJson(json: String, classOfT: Class<T>): T? {
    return mGson.fromJson(json,classOfT)
  }

  override fun toJson(src: Any): String? {
    return mGson.toJson(src)
  }
}