package com.hyc.parrot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hyc.parrot.init.Parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-08 15:49
 * @desc:
 */
open class BaseActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val bundle = intent.extras ?: Bundle()
    initBundle(bundle)
  }


  open fun initBundle(bundle: Bundle){
    Parrot.initParam(bundle,this)
  }
}