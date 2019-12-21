package com.hyc.parrot_lib

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * @author: 贺宇成
 * @date: 2019-12-21 12:58
 * @desc:
 */
class Weak<T : Any>(initializer: () -> T?) {
  var weakReference = WeakReference(initializer())

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = weakReference.get()


  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
    weakReference = WeakReference(value)
  }


}