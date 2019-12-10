package com.hyc.parrot

import com.hyc.parrot.init.InitialParam

/**
 * @author: 贺宇成
 * @date: 2019-12-08 18:58
 * @desc:
 */

data class Person(
  val name : String,
  @InitialParam("personId")
  val id : Long
)