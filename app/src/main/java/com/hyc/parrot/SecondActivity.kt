package com.hyc.parrot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.hyc.parrot.init.InitialClassParam
import com.hyc.parrot.init.InitialParam

/**
 * @author: 贺宇成
 * @date: 2019-12-08 17:29
 * @desc:
 */
class SecondActivity : BaseActivity() {

  private var int : Int = 1
  private var long : Long = 2
  private var double : Double = 3.0
  private var float : Float = 4.0f
  private var string : String = "5"
  private var userBean : UserBean? = null
  private var intString : Int = 1
  private var longString : Long = 2
  private var doubleString : Double = 3.0
  @InitialParam
  private var floatString : Float = 4.0f
  @InitialParam("jsonObject")
  private var user : UserBean? = null
  private var beike : UserBean = UserBean("贝壳","123",1,177.0f)
  @InitialClassParam
  private lateinit var student: Student

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    printParam()
    toThreeActivity()
  }

  private fun toThreeActivity() {
    val bundle = Bundle()
    bundle.putInt("int", int)
    bundle.putLong("long", long)
    bundle.putDouble("double", double)
    bundle.putFloat("float", float)
    bundle.putString("string", string)
    bundle.putString("intString", "444")
    bundle.putString("doubleString", "555")
    bundle.putString("floatString", "666")
    bundle.putString("longString", "777")
    val jsonObject = Gson().toJson(UserBean("李四","666",21,154.0f))
    bundle.putString("jsonObject", jsonObject)
    bundle.putString("name", "英语老师")
    bundle.putString("personId", "4304331885039485")
    bundle.putString("className", "软件1502")
    bundle.putString("studentId", "15508944320")
    bundle.putSerializable("userBean", userBean)
    startActivity(Intent(this, ThreeActivity::class.java).putExtras(bundle))
  }

  private fun printParam(){
    Log.d("SecondActivity","$int")
    Log.d("SecondActivity","$long")
    Log.d("SecondActivity","$double")
    Log.d("SecondActivity","$float")
    Log.d("SecondActivity", string)
    Log.d("SecondActivity","$userBean")
    Log.d("SecondActivity","$intString")
    Log.d("SecondActivity","$longString")
    Log.d("SecondActivity","$doubleString")
    Log.d("SecondActivity","$floatString")
    Log.d("SecondActivity","$user")
    Log.d("SecondActivity","$beike")
    Log.d("SecondActivity","$student")
  }


}