package com.hyc.parrot

import android.app.Application
import com.hyc.parrot_lib.DataConvert

/**
 * @author: 贺宇成
 * @date: 2019-12-13 17:32
 * @desc:
 */
class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    DataConvert.jsonConvert = MyJsonConvert()
  }


}