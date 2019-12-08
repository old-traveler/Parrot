package com.hyc.parrot

import java.io.Serializable

/**
 * @author: 贺宇成
 * @date: 2019-12-08 15:48
 * @desc:
 */
data class UserBean (
  val username : String?,
  val password : String?,
  val age : Int,
  val height : Float
) :Serializable {
  override fun toString(): String {
    return "\nusername : $username\npassword : $password\nage : $age\nheight : $height"
  }
}