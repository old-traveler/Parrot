package com.hyc.parrot.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hyc.parrot.InitialClassParam
import com.hyc.parrot.InitialParam
import java.lang.reflect.Field

/**
 * @author: 贺宇成
 * @date: 2019-12-08 15:34
 * @desc: 自动解析init传参
 */
object Parrot {

  private const val tag = "Parrot"
  private val mGson = Gson()

  public fun initParam(bundle: Bundle, any: Any) {
    initParamInternal(bundle, any)
  }

  public fun initParam(intent: Intent, any: Any) {
    intent.extras?.let {
      this.initParam(it, any)
    }
  }

  private fun initParamInternal(bundle: Bundle, any: Any, isClassParam: Boolean = false) {
    val fields = any.javaClass.declaredFields
    val keySet = bundle.keySet()
    fields?.forEach { field ->
      if (isInitialClassParam(field)) {
        field.isAccessible = true
        val param = field.get(any) ?: field.type.newInstance()
        initParamInternal(bundle, param, true)
        field.set(any, param)
      } else {
        val paramName = getParamName(field)
        val key = paramName.belongToSet(keySet)
        if (key?.isNotEmpty() == true) {
          field.isAccessible = true
          invokeField(bundle.get(key), field, any)
          keySet.remove(key)
        }
      }
    }
    if (!isClassParam) {
      keySet?.forEach {
        Log.e(tag, "key: $it not deal")
      }
    }

  }

  private fun isInitialClassParam(field: Field): Boolean {
    return field.getAnnotation(InitialClassParam::class.java) != null
  }

  private fun invokeField(data: Any?, field: Field, any: Any) {
    data ?: return
    when (field.type) {
      Int::class.java -> invokeInt(data, field, any)
      Long::class.java -> invokeLong(data, field, any)
      Float::class.java -> invokeFloat(data, field, any)
      Double::class.java -> invokeDouble(data, field, any)
      String::class.java -> invokeString(data, field, any)
      data::class.java -> invokeObject(data, field, any)
      else -> invokeJsonObject(data, field, any)
    }
  }

  private fun invokeJsonObject(data: Any, field: Field, any: Any) {
    if (data is String) {
      try {
        val filedObject = mGson.fromJson(data, field.type)
        invokeObject(filedObject, field, any)
      } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        Log.e(tag, "catch JsonSyntaxException")
      }
    } else {
      Log.e(tag, "not deal type : ${data::class.java} field type: ${field.type}")
    }
  }

  private fun invokeObject(data: Any, field: Field, any: Any) {
    field.set(any, data)
  }

  private fun invokeInt(data: Any, field: Field, any: Any) {
    when (data) {
      is Int -> field.set(any, data)
      is String -> {
        val int = data.toIntOrNull()
        int?.let { field.set(any, it) }
        int ?: Log.e(tag, "String toInt fail")
      }
      else -> Log.e(tag, "data not Int or String")
    }
  }

  private fun invokeLong(data: Any, field: Field, any: Any) {
    when (data) {
      is Long -> field.set(any, data)
      is String -> {
        val long = data.toLongOrNull()
        long?.let { field.set(any, it) }
        long ?: Log.e(tag, "String toLong fail")
      }
      else -> Log.e(tag, "data not Long or String")
    }
  }

  private fun invokeFloat(data: Any, field: Field, any: Any) {
    when (data) {
      is Float -> field.set(any, data)
      is String -> {
        val float = data.toFloatOrNull()
        float?.let { field.set(any, float) }
        float ?: Log.e(tag, "String toFloat fail")
      }
      else -> Log.e(tag, "data not Float or String")
    }
  }

  private fun invokeDouble(data: Any, field: Field, any: Any) {
    when (data) {
      is Double -> field.set(any, data)
      is String -> {
        val double = data.toDoubleOrNull()
        double?.let { field.set(any, double) }
        double ?: Log.e(tag, "String toDouble fail")
      }
      else -> Log.e(tag, "data not Double or String")
    }
  }

  private fun invokeString(data: Any, field: Field, any: Any) {
    if (data is String) {
      field.set(any, data)
    } else {
      Log.e(tag, "data not String")
    }
  }

  private fun getParamName(field: Field): ParamName {
    val initialParam = field.getAnnotation(InitialParam::class.java)
    initialParam ?: return ParamName(key = field.name)
    val fieldNames = mutableListOf<String>()
    fieldNames.add(field.name)
    fieldNames.add(initialParam.key)
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