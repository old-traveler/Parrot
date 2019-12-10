package com.hyc.parrot.init

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD

/**
 * @author: 贺宇成
 * @date: 2019-12-08 16:43
 * @desc:
 */

@kotlin.annotation.Retention(RUNTIME)
@kotlin.annotation.Target(
  FIELD
)
annotation class InitialParam(
  //Bundle解析key,可不填写，默认变量名称
  val key: String = "",
  //支持多个alternate
  val alternate: Array<String> = []
)

/**
 * 标识此参数是一个初始类型参数
 * 即可将Bundle中的数据注入此类的属性中
 */
@kotlin.annotation.Retention(RUNTIME)
@kotlin.annotation.Target(
  FIELD
)
annotation class InitialClassParam
