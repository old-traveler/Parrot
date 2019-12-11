package com.hyc.parrot.init

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hyc.parrot.BuildConfig
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.lang.reflect.Constructor
import java.lang.reflect.Field

/**
 * @author: 贺宇成
 * @date: 2019-12-08 15:34
 * @desc: 自动注入Bundle数据到属性中
 */
object Parrot {

  private const val tag = "Parrot"
  private val mGson = Gson()
  private val enableLog = BuildConfig.DEBUG

  private fun initParam(bundle: Bundle, any: Any) {
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

  private fun initParamInternal(bundle: Bundle, any: Any, isClassParam: Boolean = false) {
    val fields = any.javaClass.declaredFields
    val keySet = bundle.keySet()
    fields?.forEach { field ->
      val initialClassParam = getInitialClassParam(field)
      if (initialClassParam != null && initialClassParam.constructor.isNotEmpty()) {
        field.isAccessible = true
        val param = initClassParamConstructor(field, bundle, initialClassParam)
        param?.let {
          invokeObject(param, field, any)
          logD("data inject ${field.type} success  keys : ${initialClassParam.constructor}")
          initialClassParam.constructor.forEach { keySet.remove(it) }
        }
      } else if (initialClassParam != null) {
        field.isAccessible = true
        val param = field.get(any) ?: getParamInstance(field)
        initParamInternal(bundle, param, true)
        field.set(any, param)
      } else {
        val paramName = getParamName(field)
        val key = paramName.belongToSet(keySet)
        if (key?.isNotEmpty() == true) {
          field.isAccessible = true
          if (invokeField(bundle.get(key), field, any)) {
            keySet.remove(key)
          }
        }
      }
    }
    if (!isClassParam) {
      keySet?.forEach {
        logE("key: $it not deal in ${any::class.java.name} data : ${bundle.get(it)}")
      }
    }
  }

  private fun initClassParamConstructor(
    field: Field,
    bundle: Bundle,
    initialClassParam: InitialClassParam
  ): Any? {
    var constructor: Constructor<*>? = null
    field.type.declaredConstructors?.forEach {
      if (it.parameterTypes?.size == initialClassParam.constructor.size) {
        constructor = it
        return@forEach
      }
    }
    constructor ?: return null
    val parameterTypes = constructor?.parameterTypes
    val params = mutableListOf<Any?>()
    val length = initialClassParam.constructor.size
    for (index in 0 until length) {
      val data = bundle.get(initialClassParam.constructor[index])
      val paramType = parameterTypes?.getOrNull(index)
      if (data != null && paramType != null) {
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

  private fun getInitialClassParam(field: Field): InitialClassParam? {
    return field.getAnnotation(InitialClassParam::class.java)
  }

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

  private fun getParamName(field: Field): ParamName {
    val initialParam = field.getAnnotation(InitialParam::class.java)
    initialParam ?: return ParamName(key = field.name)
    val fieldNames = mutableListOf<String>()
    fieldNames.add(field.name)
    if (initialParam.key.isNotEmpty()) {
      fieldNames.add(initialParam.key)
    }
    fieldNames.addAll(initialParam.alternate)
    return ParamName(fieldNames = fieldNames)
  }

}

data class ParamName(
  val key: String? = null,
  val fieldNames: MutableList<String>? = null
) {

  fun belongToSet(keySet: Set<String>): String? {
    key?.let {
      if (keySet.contains(key)) {
        return key
      }
    }
    fieldNames?.forEach {
      if (keySet.contains(it)) {
        return it
      }
    }
    return null
  }

}