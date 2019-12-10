package com.hyc.parrot

import com.hyc.parrot.init.InitialClassParam
import com.hyc.parrot.init.InitialParam

/**
 * @author: 贺宇成
 * @date: 2019-12-08 20:20
 * @desc:
 */
data class Student(
  @InitialParam("class_name",alternate = ["className"])
  val class_name : String?=null,
  val studentId : String?=null,
  @InitialClassParam
  val teacher: Person?
){
  override fun toString(): String {
    return "\nclassName $class_name\nstudentId: $studentId  $teacher"
  }
}