package com.hyc.parrot

/**
 * @author: 贺宇成
 * @date: 2019-12-08 20:20
 * @desc:
 */
data class Student(
  @InitialParam("className")
  val class_name : String?=null,
  val studentId : String?=null
){
  override fun toString(): String {
    return "\nclassName $class_name\nstudentId: $studentId"
  }
}