package com.hyc.parrot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.hyc.parrot.init.InitClassParam
import com.hyc.parrot.init.InitDataStructure
import com.hyc.parrot.init.InitParam
import com.hyc.parrot.init.InjectInterceptor

/**
 * @author: 贺宇成
 * @date: 2019-12-08 17:29
 * @desc:
 */
class SecondActivity : BaseActivity() ,InjectInterceptor{
  override fun onInject(key: String, original: Any?, convertData: Any?): Boolean {
    if (key == "floatString"){
      return true
    }
    return false
  }


  private var int: String = ""
  private var long: Long = 2
  private var double: Double = 3.0
  private var float: Float = 4.0f
  private var string: String = "5"
  private var userBean: UserBean? = null
  private var intString: Int = 1
  private var longString: Long = 2
  private var doubleString: Double = 3.0
  @InitParam
  private var floatString: Float = 4.0f
  @InitParam("jsonObject")
  private var user: UserBean? = null
  private var beike: UserBean = UserBean("贝壳", "123", 1, 177.0f)
  @InitClassParam
  private lateinit var student: Student
  @InitDataStructure("int", "intString")
  private lateinit var intArray: Array<Int>
  @InitDataStructure("int", "intString")
  private lateinit var intArray1: IntArray

  @InitDataStructure("int", "string")
  private lateinit var stringList: List<String?>
  @InitDataStructure("long", "float", "intString", "doubleString")
  private lateinit var numberSet: Set<Double>
  @InitDataStructure(
    value = ["double", "longString", "floatString"],
    mapKey = ["intKey", "longKey"]
  )
  private lateinit var map: Map<String, Double?>
  @InitDataStructure(
    value = ["int", "long", "double", "longString", "floatString"],
    mapKey = ["intKey", "longKey"]
  )
  private lateinit var bundle: Bundle

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    printParam()
    toThreeActivity()
  }

  private fun toThreeActivity() {
    val bundle = Bundle()
    bundle.putInt("int", int.toInt())
    bundle.putLong("long", long)
    bundle.putDouble("double", double)
    bundle.putFloat("float", float)
    bundle.putString("string", string)
    bundle.putString("intString", "444")
    bundle.putString("doubleString", "555")
    bundle.putString("floatString", "666")
    bundle.putString("longString", "777")
    val jsonObject = Gson().toJson(UserBean("李四", "666", 21, 154.0f))
    bundle.putString("jsonObject", jsonObject)
    bundle.putString("name", "英语老师")
    bundle.putString("personId", "4304331885039485")
    bundle.putString("className", "软件1502")
    bundle.putDouble("str1", 116.382094)
    bundle.putDouble("str2", 98.38024)
    bundle.putString("studentId", "15508944320")
    bundle.putSerializable("userBean", userBean)
    Handler().postDelayed({
      startActivity(Intent(this, ThreeActivity::class.java).putExtras(bundle))
    }, 3000)
  }

  private fun printParam() {
    Log.d("SecondActivity", "$int")
    Log.d("SecondActivity", "$long")
    Log.d("SecondActivity", "$double")
    Log.d("SecondActivity", "$float")
    Log.d("SecondActivity", string)
    Log.d("SecondActivity", "$userBean")
    Log.d("SecondActivity", "$intString")
    Log.d("SecondActivity", "$longString")
    Log.d("SecondActivity", "$doubleString")
    Log.d("SecondActivity", "$floatString")
    Log.d("SecondActivity", "$user")
    Log.d("SecondActivity", "$beike")
    Log.d("SecondActivity", "$student")
    intArray.forEach {
      Log.d("SecondActivity","intArray: $it")
    }
    intArray1.forEach {
      Log.d("SecondActivity","intArray1: $it")
    }
    stringList.forEach {
      Log.d("SecondActivity","stringList: $it")
    }
    numberSet.forEach{
      Log.d("SecondActivity","numberSet: $it")
    }
    map.forEach {
      Log.d("SecondActivity","key: ${it.key}  value ${it.value}")
    }
    bundle.keySet()?.forEach {
      Log.d("SecondActivity","bundleKey: $it  value ${bundle.get(it)}")
    }
  }

}