package com.hyc.parrot

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
  /**
   * @return the desired name of the field when it is serialized or deserialized
   */
  val value: String,
  /**
   * @return the alternative names of the field when it is deserialized
   */
  val alternate: Array<String> = []
)