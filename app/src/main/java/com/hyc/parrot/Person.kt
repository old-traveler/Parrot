package com.hyc.parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-08 18:58
 * @desc:
 */

data class Person(
  val name : String,
  @InitialParam("personId")
  val id : Long
){
  override fun toString(): String {
    return "\nname：${name} id：$id"
  }
}