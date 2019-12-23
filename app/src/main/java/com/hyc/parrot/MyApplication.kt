package com.hyc.parrot

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.hyc.parrot_lib.Parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-13 17:32
 * @desc:
 */
class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    context = this.applicationContext
    Parrot.initJsonConvert(MyJsonConvert())
    Parrot.initDefaultPrefixProvider(MyPrefixProvider())
  }

  companion object {
    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
  }

}