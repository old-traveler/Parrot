package com.hyc.parrot

import com.hyc.parrot_lib.InitParam

/**
 * @author: 贺宇成
 * @date: 2019-12-08 18:58
 * @desc:
 */

data class Person(
  val name : String,
  @InitParam("personId")
  val id : Long
)