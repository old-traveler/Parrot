package com.hyc.parrot

import android.content.Context
import android.content.SharedPreferences
import com.hyc.parrot_lib.ISharedPreferences

/**
 * @author: 贺宇成
 * @date: 2019-12-21 14:44
 * @desc:
 */
class MySharedPreferences : ISharedPreferences {

  override fun getSharedPreferences(): SharedPreferences {
    return MyApplication.context.getSharedPreferences("parrot", Context.MODE_PRIVATE)
  }
}