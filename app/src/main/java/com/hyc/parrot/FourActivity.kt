package com.hyc.parrot

import android.os.Bundle
import com.hyc.parrot_lib.InitCache
import com.hyc.parrot_lib.InitParam
import kotlinx.android.synthetic.main.activity_four.tv_tip
import kotlinx.android.synthetic.main.activity_four.tv_user

/**
 * @author: 贺宇成
 * @date: 2019-12-23 10:04
 * @desc:
 */
class FourActivity : BaseActivity() {

  @InitCache("curUser", onlyRead = true, spName = "user")
  private lateinit var curUser: UserBean

  @InitParam("date")
  private lateinit var date: String

  @InitCache("loginCount", prefixField = "date")
  private var loginCount = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_four)
    tv_user.text = curUser.toString()
    loginCount++
    curUser = UserBean("李四", "123", 0, 0.0f)
    tv_tip.text = "$date 登录人数：$loginCount"
  }

}