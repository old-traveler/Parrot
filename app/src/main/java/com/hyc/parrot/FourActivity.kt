package com.hyc.parrot

import android.os.Bundle
import com.hyc.parrot_lib.InitCache
import kotlinx.android.synthetic.main.activity_four.tv_user

/**
 * @author: 贺宇成
 * @date: 2019-12-23 10:04
 * @desc:
 */
class FourActivity : BaseActivity() {

  @InitCache("curUser", onlyRead = true)
  private lateinit var curUser: UserBean

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_four)
    tv_user.text = curUser.toString()
    curUser = UserBean("李四", "123", 0, 0.0f)
  }

}