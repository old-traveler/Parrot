package com.hyc.parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-08 18:58
 * @desc:
 */

data class Person(
  var name : String? = null,
  @InitialParam("personId")
  var id : String? = null
){
  override fun toString(): String {
    return "name：${name} id：$id"
  }
}