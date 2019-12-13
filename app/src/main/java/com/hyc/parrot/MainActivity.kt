package com.hyc.parrot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.hyc.parrot.init.DataConvert
import com.hyc.parrot.init.InitParam

class MainActivity : BaseActivity() {

  @InitParam
  private var int: Int = Int.MAX_VALUE
  private var long: Long = Long.MAX_VALUE
  private var double: Double = Double.MAX_VALUE
  private var float: Float = Float.MAX_VALUE
  private var string: String = "String"
  private var userBean: UserBean = UserBean("张三", "123", 22, 165.0f)
  private var intString: String = "4324"
  private var longString: String = "444"
  private var doubleString: String = "555"
  private var floatString: String = "666"
  private var jsonObject: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    DataConvert.jsonConvert = MyJsonConvert()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    printParam()
    val bundle = Bundle()
    bundle.putInt("int", int)
    bundle.putLong("long", long)
    bundle.putDouble("double", double)
    bundle.putFloat("float", float)
    bundle.putString("string", string)
    bundle.putString("intString", intString)
    bundle.putString("doubleString", doubleString)
    bundle.putString("floatString", floatString)
    bundle.putString("longString", longString)
    jsonObject = Gson().toJson(UserBean("李四", "666", 21, 154.0f))
    bundle.putString("jsonObject", jsonObject)
    bundle.putString("name", "英语老师")
    bundle.putString("personId", "4304331885039485")
    bundle.putString("className", "软件1502")
    bundle.putString("studentId", "15508944320")
    bundle.putSerializable("userBean", userBean)
    startActivity(Intent(this, SecondActivity::class.java).putExtras(bundle))
  }

  private fun printParam() {
    Log.d("MainActivity", "$int")
    Log.d("MainActivity", "$long")
    Log.d("MainActivity", "$double")
    Log.d("MainActivity", "$float")
    Log.d("MainActivity", string)
    Log.d("MainActivity", "$userBean")
    Log.d("MainActivity", "$intString")
    Log.d("MainActivity", "$longString")
    Log.d("MainActivity", "$doubleString")
    Log.d("MainActivity", "$floatString")
    Log.d("MainActivity", "$jsonObject")
  }

}
