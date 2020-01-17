package com.hyc.parrot_lib

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
annotation class InitParam(
  /**
   * Bundle解析参数key,可设置多个key
   */
  vararg val value: String = []
)

/**
 * 标识此参数是一个初始类型参数
 * 即可将Bundle中的数据注入此类的属性中
 */
@kotlin.annotation.Retention(RUNTIME)
@kotlin.annotation.Target(
  FIELD
)
annotation class InitClassParam(
  vararg val value: String = []
)

//支持 List、Set、Array、Map、Bundle
@kotlin.annotation.Retention(RUNTIME)
@kotlin.annotation.Target(
  FIELD
)
annotation class InitDataStructure(
  /**
   * Bundle解析参数key
   */
  vararg val value: String = [],
  /**
   * map or Bundle 使用时可设置对应的mapKey
   */
  val mapKey: Array<String> = []
)


@kotlin.annotation.Retention(RUNTIME)
@kotlin.annotation.Target(
  FIELD
)
annotation class InitCache(
  vararg val value: String = [],
  val spName: String = "", //指定改属性存储的sp文件名
  val prefixKey: String = "", //设置属性存储key前缀表识并通过前缀提供接口返回其所需前缀
  val onlyRead: Boolean = false //是否为只读变量，只读变量只做数据的读取，不会报错属性的修改
)