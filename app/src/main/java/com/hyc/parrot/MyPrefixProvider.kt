package com.hyc.parrot

import android.annotation.SuppressLint
import com.hyc.parrot_lib.PrefixProvider
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author: 贺宇成
 * @date: 2019-12-23 09:44
 * @desc:
 */
class MyPrefixProvider : PrefixProvider {

  companion object {
    const val DATE = "date"
  }

  @SuppressLint("SimpleDateFormat")
  override fun getKeyPrefix(key: String, prefixKey: String): String {
    if (prefixKey == DATE) {
      val date = Date()
      val format = SimpleDateFormat("yyyy-MM-dd")
      return format.format(date)
    }
    return ""
  }
}