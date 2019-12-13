package com.hyc.parrot

import com.hyc.parrot_lib.InitClassParam
import com.hyc.parrot_lib.InitParam

/**
 * @author: 贺宇成
 * @date: 2019-12-08 20:20
 * @desc:
 */
data class Student(
  @InitParam("class_name","className")
  val class_name : String?=null,
  val studentId : String?=null,
  @InitClassParam
  val teacher: Person?
){
  override fun toString(): String {
    return "\nclassName $class_name\nstudentId: $studentId  $teacher"
  }
}