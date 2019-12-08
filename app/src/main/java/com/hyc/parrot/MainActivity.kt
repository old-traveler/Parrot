package com.hyc.parrot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson

class MainActivity : BaseActivity() {

  private var int: Int = Int.MAX_VALUE
  private var long: Long = Long.MAX_VALUE
  private var double: Double = Double.MAX_VALUE
  private var float: Float = Float.MAX_VALUE
  private var string: String = "String"
  private var userBean: UserBean = UserBean("贺宇成", "123", 22, 165.0f)
  private var intString : String = "int解析错误"
  private var longString : String = "444"
  private var doubleString : String = "555"
  private var floatString : String = "666"
  private var jsonObject : String = ""


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
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
    jsonObject = Gson().toJson(UserBean("杨静","666",21,154.0f))
    bundle.putString("jsonObject", jsonObject)
    bundle.putSerializable("userBean", userBean)
    startActivity(Intent(this, SecondActivity::class.java).putExtras(bundle))
  }

}
