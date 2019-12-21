package com.hyc.parrot_lib

import android.content.SharedPreferences
import com.hyc.parrot_lib.Parrot.getActualType
import com.hyc.parrot_lib.Parrot.logD
import java.lang.RuntimeException
import java.lang.reflect.Field

/**
 * @author: 贺宇成
 * @date: 2019-12-21 12:34
 * @desc:
 */
@Suppress("UNCHECKED_CAST")
class CacheAdapter(private val dataConvert: DataConvert) {

  var sharedPreferencesClass: Class<*>? = null

  private var mSharedPreferences by Weak<SharedPreferences> { null }

  private fun getSharedPreferences(): SharedPreferences {
    mSharedPreferences =
      mSharedPreferences
        ?: (sharedPreferencesClass!!.newInstance() as ISharedPreferences).getSharedPreferences()
    return mSharedPreferences!!
  }

  inline fun isCacheParam(field: () -> Field): InitCache? {
    return field().getAnnotation(InitCache::class.java)
  }

  fun initCacheParam(any: Any, field: Field, initCache: InitCache) {
    if (!field.isAccessible) field.isAccessible = true
    when {
      initCache.value.isEmpty() -> throw RuntimeException("initCache must have a key")
      initCache.value.size == 1 -> {
        getDataByClass(field.type, initCache.value[0], field.get(any))?.let {
          field.set(any, it)
        }
      }
      field.type.isArray -> initArray(any, field, initCache)
      else -> when (field.type) {
        List::class.java -> initList(any, field, initCache)
        Map::class.java -> initMap(any, field, initCache)
        else -> throw RuntimeException("unknown type ${field.type}")
      }
    }
  }

  fun saveCacheParam(any: Any) {
    val startTime = System.currentTimeMillis()
    val fields = any.javaClass.declaredFields
    fields.forEach { field ->
      isCacheParam { field }?.let { saveCacheParamInternal(any, field, it) }
    }
    logD("saveCacheParam cost ${System.currentTimeMillis() - startTime}")
  }

  private fun saveCacheParamInternal(any: Any, field: Field, initCache: InitCache) {
    if (!field.isAccessible) field.isAccessible = true
    when {
      initCache.value.isEmpty() -> throw RuntimeException("initCache must have a key")
      initCache.value.size == 1 -> {
        val editor = getSharedPreferences().edit()
        saveDataByClass(field.type, initCache.value[0], field.get(any), editor)
        editor.apply()
      }
      field.type.isArray -> {
        saveArray(field.get(any), field.getActualType { Any::class.java }, initCache)
      }
      else -> when (field.type) {
        List::class.java -> saveList(
          field.get(any) as List<*>,
          field.getActualType { Any::class.java },
          initCache
        )
        Map::class.java -> saveMap(
          field.get(any) as Map<String, *>,
          field.getActualType { Any::class.java },
          initCache
        )
        else -> throw RuntimeException("unknown type ${field.type}")
      }
    }
  }

  private fun saveList(list: List<*>, clazz: Class<*>, initCache: InitCache) {
    val editor = getSharedPreferences().edit()
    var index = 0
    initCache.value.forEach { key ->
      list[index++]?.let { saveDataByClass(clazz, key, it, editor) }
    }
    editor.apply()
  }

  private fun saveMap(map: Map<String, *>, clazz: Class<*>, initCache: InitCache) {
    val editor = getSharedPreferences().edit()
    initCache.value.forEach { key ->
      map[key]?.let { saveDataByClass(clazz, key, it, editor) }
    }
    editor.apply()
  }

  private fun saveArray(array: Any, clazz: Class<*>, initCache: InitCache) {
    val editor = getSharedPreferences().edit()
    var index = 0
    when (array) {
      is Array<*> -> {
        initCache.value.forEach { key ->
          array[index++]?.let {
            saveDataByClass(clazz, key, it, editor)
          }
        }
      }
      is IntArray -> {
        initCache.value.forEach { key ->
          saveDataByClass(clazz, key, array[index++], editor)
        }
      }
      is LongArray -> {
        initCache.value.forEach { key ->
          saveDataByClass(clazz, key, array[index++], editor)
        }
      }
      is FloatArray -> {
        initCache.value.forEach { key ->
          saveDataByClass(clazz, key, array[index++], editor)
        }
      }
      is BooleanArray -> {
        initCache.value.forEach { key ->
          saveDataByClass(clazz, key, array[index++], editor)
        }
      }
    }
    editor.apply()
  }

  private fun initArray(any: Any, field: Field, initCache: InitCache) {
    val clazz = field.getActualType { Any::class.java }
    val list = mutableListOf<Any?>()
    initCache.value.forEach {
      list.add(getDataByClass(clazz, it, dataConvert.getDefaultValue(clazz)))
    }
    field.set(any, dataConvert.toArray(list, clazz))
  }

  private fun initList(any: Any, field: Field, initCache: InitCache) {
    val clazz = field.getActualType { Any::class.java }
    val list = mutableListOf<Any?>()
    initCache.value.forEach {
      list.add(getDataByClass(clazz, it, dataConvert.getDefaultValue(clazz)))
    }
    field.set(any, list)
  }

  private fun initMap(any: Any, field: Field, initCache: InitCache) {
    val clazz = field.getActualType { Any::class.java }
    val map = mutableMapOf<String, Any?>()
    initCache.value.forEach {
      map[it] = getDataByClass(clazz, it, dataConvert.getDefaultValue(clazz))
    }
    field.set(any, map)
  }

  private fun getDataByClass(clazz: Class<*>, key: String, default: Any?): Any? {
    val sharedPreferences = getSharedPreferences()
    return when (clazz) {
      Int::class.java -> sharedPreferences.getInt(key, default as Int)
      Float::class.java -> sharedPreferences.getFloat(key, default as Float)
      Long::class.java -> sharedPreferences.getLong(key, default as Long)
      Boolean::class.java -> sharedPreferences.getBoolean(key, default as Boolean)
      String::class.java -> sharedPreferences.getString(key, default as String)
      MutableSet::class.java -> sharedPreferences.getStringSet(key, default as MutableSet<String>)
      else -> {
        sharedPreferences.getString(key, null)?.let {
          dataConvert.getJsonObject(it, clazz)
        }
      }
    }
  }

  private fun saveDataByClass(
    clazz: Class<*>,
    key: String,
    value: Any,
    editor: SharedPreferences.Editor
  ) {
    when (clazz) {
      Int::class.java -> editor.putInt(key, value as Int)
      Float::class.java -> editor.putFloat(key, value as Float)
      Long::class.java -> editor.putLong(key, value as Long)
      Boolean::class.java -> editor.putBoolean(key, value as Boolean)
      String::class.java -> editor.putString(key, value as String)
      MutableSet::class.java -> editor.putStringSet(key, value as MutableSet<String>)
      else -> {
        editor.putString(key, dataConvert.jsonConvert?.toJson(value))
      }
    }
  }

}

interface ISharedPreferences {

  fun getSharedPreferences(): SharedPreferences

}