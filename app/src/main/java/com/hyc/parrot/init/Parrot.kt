@file:Suppress("UNCHECKED_CAST")

package com.hyc.parrot.init

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hyc.parrot.BuildConfig
import java.io.Serializable
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

/**
 * @author: 贺宇成
 * @date: 2019-12-08 15:34
 * @desc: 自动注入Bundle数据到属性中
 */
object Parrot {

  private const val tag = "Parrot"
  private val mGson = Gson()
  private val enableLog = BuildConfig.DEBUG
  /**
   * 是否允许用field的name作为bundleKey
   */
  private var enableNameKey = true

  @JvmStatic
  fun initParam(bundle: Bundle, any: Any) {
    val startTime = System.currentTimeMillis()
    initParamInternal(bundle, any)
    logD("initParam cost ${System.currentTimeMillis() - startTime}")
  }

  private fun logD(msg: String?) {
    if (enableLog) {
      Log.d(tag, msg)
    }
  }

  private fun logE(msg: String?) {
    if (enableLog) {
      Log.e(tag, msg)
    }
  }

  @JvmStatic
  fun initParam(any: Any) {
    var bundle: Bundle? = null
    if (any is Activity) {
      bundle = any.intent?.extras
    } else if (any is Fragment) {
      bundle = any.arguments
    }
    bundle?.let {
      initParam(it, any)
    }
  }

  private fun initParamInternal(
    bundle: Bundle,
    any: Any,
    recursiveSet: MutableMap<String, Boolean>? = null
  ) {
    val fields = any.javaClass.declaredFields
    val keyMap = recursiveSet ?: bundle.toKeyMap()
    fields?.forEach { field ->
      val dataStructure = getInitDataStructure(field)
      val initialClassParam = if (dataStructure == null) getInitialClassParam(field) else null
      when {
        dataStructure != null -> //对数据结构类型的field进行数据注入
          if (injectDataStructure(field, bundle, any, dataStructure)) {
            keyMap.signDeal(*dataStructure.value)
          }
        initialClassParam != null && initialClassParam.value.isNotEmpty() -> {
          //对此field进行构造方法数据注入
          val param = initClassParamConstructor(field, bundle, initialClassParam, any)
          param?.let {
            invokeObject(param, field, any)
            keyMap.signDeal(*initialClassParam.value)
          }
        }
        initialClassParam != null -> {
          //此field为一个类型参数，可将bundle数据注入到此对象中
          field.isAccessible = true
          val param = field.get(any) ?: getParamInstance(field)
          initParamInternal(bundle, param, keyMap)
          invokeObject(param, field, any)
        }
        else -> {
          //针对单个Field进行检查，如果匹配到key，则进行注入
          val paramName = getParamName(field)
          val key = paramName?.belongToSet(keyMap)
          val data = bundle.get(key)
          if (key?.isNotEmpty() == true && !any.isIntercept(key, data)) {
            field.isAccessible = true
            if (invokeField(data, field, any)) {
              keyMap.signDeal(key)
            }
          }
        }
      }
    }

    //打印未处理的key
    recursiveSet ?: keyMap.forEach {
      if (!it.value) {
        logE("key: ${it.key} not deal in ${any::class.java.name} data : ${bundle.get(it.key)}")
      }
    }
  }

  private fun Bundle.toKeyMap(): MutableMap<String, Boolean> {
    val keyMap = mutableMapOf<String, Boolean>()
    this.keySet().forEach {
      keyMap[it] = false
    }
    return keyMap
  }

  private fun MutableMap<String, Boolean>.signDeal(vararg keys: String) {
    keys.forEach {
      logD("key : \"$it\"  has been processed")
      this[it] = true
    }
  }

  /**
   * 获取当前注入目标对象是否为拦截器类型，如果是拦截器类型并且拦截注入事件
   * 则停止注入行为，跳过此key。{跳过的key也会被认为已处理，不打印错误信息}
   */
  private fun Any.isIntercept(key: String, any: Any?): Boolean {
    if (this is InjectInterceptor) {
      val isIntercept = this.onInject(key, any)
      if (isIntercept) {
        logD("key : $key has been Intercepted")
      }
      return isIntercept
    }
    return false
  }

  private fun injectDataStructure(
    field: Field,
    bundle: Bundle,
    any: Any,
    initialMapParam: InitDataStructure
  ): Boolean {
    if (initialMapParam.value.isEmpty()) return false
    field.isAccessible = true
    val dataList = mutableListOf<Any?>()
    initialMapParam.value.forEach { key ->
      val value = bundle.get(key)
      if (!any.isIntercept(key, value)) {
        dataList.add(value)
      }
    }
    if (field.type.isArray) {
      return injectArray(dataList, field, any)
    }
    when (field.type) {
      List::class.java -> return injectList(dataList, field, any)
      Set::class.java -> return injectSet(dataList, field, any)
      Map::class.java -> return injectMap(dataList, field, any, initialMapParam)
      Bundle::class.java -> return injectBundle(dataList, field, any, initialMapParam)
    }
    return false
  }

  private fun Field.getActualType(): Class<*>? {
    return (this.genericType as? ParameterizedType)?.actualTypeArguments
      ?.getOrNull(if (this.type == Map::class.java) 1 else 0) as? Class<*>
  }

  private fun MutableList<Any?>.typeConversion(clazz: Class<*>) {
    val resList = map {
      it?.let {
        when {
          clazz == String::class.java -> it.toString()
          getType(it) == String::class.java -> getDataFromString(clazz, it as String)
          else -> it
        }
      }
    }
    this.clear()
    this.addAll(resList)
  }

  private fun MutableList<Any?>.toArray(clazz: Class<*>): Any? {
    val length = this.size
    when (clazz) {
      String::class.java -> return (this as? MutableList<String?>)?.toTypedArray()
      Int::class.java -> {
        val array = (this as? MutableList<Int?>)?.toTypedArray()
        return IntArray(length) { i -> array?.get(i) ?: 0 }
      }
      Integer::class.java -> return (this as? MutableList<Int?>)?.toTypedArray()
      java.lang.Double::class.java -> return (this as? MutableList<Double?>)?.toTypedArray()
      Double::class.java -> {
        val array = (this as? MutableList<Double?>)?.toTypedArray()
        return DoubleArray(length) { i -> array?.get(i) ?: 0.0 }
      }
      java.lang.Float::class.java -> return (this as? MutableList<Float?>)?.toTypedArray()
      Float::class.java -> {
        val array = (this as? MutableList<Float?>)?.toTypedArray()
        return FloatArray(length) { i -> array?.get(i) ?: 0.0f }
      }
      java.lang.Byte::class.java -> return (this as? MutableList<Byte?>)?.toTypedArray()
      Byte::class.java -> {
        val array = (this as? MutableList<Byte?>)?.toTypedArray()
        return ByteArray(length) { i -> array?.get(i) ?: 0 }
      }
      java.lang.Long::class.java -> return (this as? MutableList<Long?>)?.toTypedArray()
      Long::class.java -> {
        val array = (this as? MutableList<Long?>)?.toTypedArray()
        return LongArray(length) { i -> array?.get(i) ?: 0 }
      }
      java.lang.Character::class.java -> return (this as? MutableList<Char?>)?.toTypedArray()
      Char::class.java -> {
        val array = (this as? MutableList<Char?>)?.toTypedArray()
        return CharArray(length) { i -> array?.get(i) ?: ' ' }
      }
      java.lang.Boolean::class.java -> return (this as? MutableList<Boolean?>)?.toTypedArray()
      Boolean::class.java -> {
        val array = (this as? MutableList<Boolean?>)?.toTypedArray()
        return BooleanArray(length) { i -> array?.get(i) ?: false }
      }
      java.lang.Short::class.java -> return (this as? MutableList<Short?>)?.toTypedArray()
      Short::class.java -> {
        val array = (this as? MutableList<Short?>)?.toTypedArray()
        return ShortArray(length) { i -> array?.get(i) ?: 0 }
      }
      Parcelable::class.java -> return (this as? MutableList<Parcelable?>)?.toTypedArray()
      Serializable::class.java -> return (this as? MutableList<Serializable?>)?.toTypedArray()
    }
    return null
  }

  private fun injectArray(dataList: MutableList<Any?>, field: Field, any: Any): Boolean {
    dataList.typeConversion(field.type.componentType)
    dataList.toArray(field.type.componentType)?.let {
      invokeObject(it, field, any)
      return true
    }
    return false
  }

  private fun injectList(dataList: MutableList<Any?>, field: Field, any: Any): Boolean {
    field.getActualType()?.let { dataList.typeConversion(it) }
    val list: MutableList<Any?> = field.get(any) as? MutableList<Any?> ?: mutableListOf()
    list.addAll(dataList)
    invokeObject(list, field, any)
    return true
  }

  private fun injectSet(dataList: MutableList<Any?>, field: Field, any: Any): Boolean {
    field.getActualType()?.let { dataList.typeConversion(it) }
    val set: MutableSet<Any?> = field.get(any) as? MutableSet<Any?> ?: mutableSetOf()
    set.addAll(dataList)
    invokeObject(set, field, any)
    return true
  }

  private fun injectMap(
    dataList: MutableList<Any?>,
    field: Field,
    any: Any,
    initDataStructure: InitDataStructure
  ): Boolean {
    val length = dataList.size
    field.getActualType()?.let {
      dataList.typeConversion(it)
    }
    val map: MutableMap<String, Any?> =
      field.get(any) as? MutableMap<String, Any?> ?: mutableMapOf()
    for (index in 0 until length) {
      map[initDataStructure.mapKey.getOrElse(index) { initDataStructure.value[index] }] =
        dataList[index]
    }
    invokeObject(map, field, any)
    return true
  }

  private fun injectBundle(
    dataList: MutableList<Any?>,
    field: Field,
    any: Any,
    initDataStructure: InitDataStructure
  ): Boolean {
    val bundle = field.get(any) as? Bundle ?: Bundle()
    var index = 0
    dataList.forEach {
      val key = initDataStructure.mapKey.getOrElse(index) { initDataStructure.value[index] }
      when (it) {
        is Int -> bundle.putInt(key, it)
        is Float -> bundle.putFloat(key, it)
        is Byte -> bundle.putByte(key, it)
        is Double -> bundle.putDouble(key, it)
        is Long -> bundle.putLong(key, it)
        is Char -> bundle.putChar(key, it)
        is Boolean -> bundle.putBoolean(key, it)
        is Short -> bundle.putShort(key, it)
        is String -> bundle.putString(key, it)
        is Parcelable -> bundle.putParcelable(key, it)
        is Serializable -> bundle.putSerializable(key, it)
        is Bundle -> bundle.putBundle(key, it)
        is IntArray -> bundle.putIntArray(key, it)
        is FloatArray -> bundle.putFloatArray(key, it)
        is ByteArray -> bundle.putByteArray(key, it)
        is DoubleArray -> bundle.putDoubleArray(key, it)
        is LongArray -> bundle.putLongArray(key, it)
        is CharArray -> bundle.putCharArray(key, it)
        is BooleanArray -> bundle.putBooleanArray(key, it)
        is ShortArray -> bundle.putShortArray(key, it)
        is Array<*> -> bundle.putStringArrays(key, it)
        is ArrayList<*> -> bundle.putArrayList(key, it)
      }
      index++
    }
    invokeObject(bundle, field, any)
    return true
  }

  private fun Bundle.putStringArrays(key: String, array: Array<*>) {
    if (array::class.java.componentType == String::class.java) {
      putStringArray(key, array as? Array<String>?)
    } else if (array::class.java.componentType == Parcelable::class.java) {
      putParcelableArray(key, array as? Array<Parcelable>?)
    }
  }

  private fun Bundle.putArrayList(key: String, array: ArrayList<*>) {
    when (array.getOrNull(0)) {
      is String -> putStringArrayList(key, array as? ArrayList<String>?)
      is Int -> putIntegerArrayList(key, array as? ArrayList<Int>)
      is Parcelable -> putParcelableArrayList(key, array as? ArrayList<Parcelable>)
      is CharSequence -> putCharSequenceArrayList(key, array as? ArrayList<CharSequence>)
    }
  }

  private fun getInitDataStructure(field: Field): InitDataStructure? {
    return field.getAnnotation(InitDataStructure::class.java)
  }

  private fun initClassParamConstructor(
    field: Field,
    bundle: Bundle,
    initialClassParam: InitClassParam,
    any: Any
  ): Any? {
    field.isAccessible = true
    var constructor: Constructor<*>? = null
    field.type.declaredConstructors?.forEach {
      if (it.parameterTypes?.size == initialClassParam.value.size) {
        constructor = it
        return@forEach
      }
    }
    constructor ?: return null
    val parameterTypes = constructor?.parameterTypes
    val params = mutableListOf<Any?>()
    val length = initialClassParam.value.size
    for (index in 0 until length) {
      val key = initialClassParam.value[index]
      val data = bundle.get(key)
      val paramType = parameterTypes?.getOrNull(index)
      if (data != null && paramType != null && !any.isIntercept(key, data)) {
        when (getType(data)) {
          paramType -> params.add(data)
          String::class.java -> params.add(getDataFromString(paramType, data as String))
          else -> params.add(null)
        }
      } else {
        params.add(null)
      }
    }
    return constructor?.newInstance(*params.toTypedArray())
  }

  private fun getDataFromString(clazz: Class<*>, string: String): Any? {
    try {
      when (clazz) {
        Int::class.java -> return string.toInt()
        Float::class.java -> return string.toFloat()
        Byte::class.java -> return string.toByte()
        Double::class.java -> return string.toDouble()
        Long::class.java -> return string.toLong()
        Boolean::class.java -> return string.toBoolean()
        Short::class.java -> return string.toShort()
        String::class.java -> return string
        Char::class.java -> {
          return if (string.length == 1) {
            string[0]
          } else {
            null
          }
        }
        else -> return getJsonObject(string, clazz)
      }
    } catch (e: NumberFormatException) {
      e.printStackTrace()
      logE("String to $clazz catch NumberFormatException data: $string")
    }
    return null
  }

  private fun getParamInstance(
    field: Field
  ): Any {
    if (field.type.declaredConstructors.isNullOrEmpty()) {
      throw RuntimeException("class :${field.type} must have a constructor")
    }
    val constructor = field.type.declaredConstructors[0]
    val parameterTypes = constructor.parameterTypes
    if (parameterTypes.isNullOrEmpty()) {
      return constructor.newInstance()
    }
    val param = mutableListOf<Any?>()
    constructor.parameterTypes?.forEach {
      val value: Any? = when (it) {
        Int::class.java -> 0
        Float::class.java -> 0
        Byte::class.java -> 0
        Double::class.java -> 0
        Long::class.java -> 0L
        Char::class.java -> 0
        Boolean::class.java -> false
        Short::class.java -> 0
        String::class.java -> ""
        else -> null
      }
      param.add(value)
    }
    return constructor.newInstance(*param.toTypedArray())
  }

  private fun getInitialClassParam(field: Field): InitClassParam? {
    return field.getAnnotation(InitClassParam::class.java)
  }

  /**
   * 通过is来判断此数据的类型
   * 用来兼容Java和Kotlin的基本类型无法直接对比的问题
   */
  private fun getType(any: Any): Class<*> {
    when (any) {
      is Int -> return Int::class.java
      is Float -> return Float::class.java
      is Byte -> return Byte::class.java
      is Double -> return Double::class.java
      is Long -> return Long::class.java
      is Char -> return Char::class.java
      is Boolean -> return Boolean::class.java
      is Short -> return Short::class.java
      is String -> return String::class.java
    }
    return any::class.java
  }

  private fun invokeField(data: Any?, field: Field, any: Any): Boolean {
    data ?: return false
    if (getType(data) == field.type) {
      invokeObject(data, field, any)
    } else if (data::class.java == String::class.java && stringToData(
        field,
        data as? String,
        any
      )
    ) {
      logD("string to ${field.type} success  data: $data")
    } else if (field.type == String::class.java) {
      invokeObject(data.toString(), field, any)
    } else {
      return false
    }
    return true
  }

  private fun stringToData(field: Field, string: String?, any: Any): Boolean {
    if (string.isNullOrEmpty()) {
      return false
    }
    getDataFromString(field.type, string)?.let {
      invokeObject(it, field, any)
      return true
    }
    return false
  }

  private fun getJsonObject(data: String?, type: Class<*>): Any? {
    data ?: return null
    var filedObject: Any? = null
    try {
      filedObject = mGson.fromJson(data, type)
    } catch (e: JsonSyntaxException) {
      e.printStackTrace()
      logE("parse $type catch JsonSyntaxException json :$data")
    }
    return filedObject
  }

  private fun invokeObject(data: Any, field: Field, any: Any) {
    field.set(any, data)
  }

  private fun getParamName(field: Field): ParamName? {
    val initialParam = field.getAnnotation(InitParam::class.java)
    initialParam ?: return if (enableNameKey) ParamName(key = field.name) else null
    val fieldNames = mutableListOf<String>()
    fieldNames.add(field.name)
    if (initialParam.value.isNotEmpty()) {
      fieldNames.addAll(initialParam.value)
    }
    return ParamName(fieldNames = fieldNames)
  }

}

data class ParamName(
  val key: String? = null,
  val fieldNames: MutableList<String>? = null
) {

  fun belongToSet(keySet: Map<String, Boolean>): String? {
    key?.let {
      if (keySet.containsKey(key)) {
        return key
      }
    }
    fieldNames?.forEach {
      if (keySet.containsKey(it)) {
        return it
      }
    }
    return null
  }

}

interface InjectInterceptor {
  /**
   * 注入参数时的回调方法，在被注入的class中实现此接口
   * 即可接收到注入事件回掉，可通过返回值拦截注入事件
   * @return true 为拦截此注入事件 otherwise 继续注入
   */
  fun onInject(key: String, any: Any?): Boolean
}