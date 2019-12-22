package com.hyc.parrot

import android.os.Bundle
import android.support.v4.app.Fragment
import com.hyc.parrot_lib.Parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-10 12:59
 * @desc:
 */
open class BaseFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Parrot.initParam(this)
  }

  override fun onPause() {
    super.onPause()
  }

}