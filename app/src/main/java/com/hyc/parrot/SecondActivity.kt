package com.hyc.parrot

import android.os.Bundle
import android.util.Log

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
  private var string1 : String = "5"
  private var userBean : UserBean? = null
  private var intString : Int = 1
  private var longString : Long = 2
  private var doubleString : Double = 3.0
  private var floatString : Float = 4.0f
  @InitialParam("jsonObject")
  private var user : UserBean? = null
  private var beike : UserBean = UserBean("贝壳","123",1,177.0f)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    printParam()
  }

  private fun printParam(){
    Log.d("SecondActivity","$int")
    Log.d("SecondActivity","$long")
    Log.d("SecondActivity","$double")
    Log.d("SecondActivity","$float")
    Log.d("SecondActivity", string1)
    Log.d("SecondActivity","$userBean")
    Log.d("SecondActivity","$intString")
    Log.d("SecondActivity","$longString")
    Log.d("SecondActivity","$doubleString")
    Log.d("SecondActivity","$floatString")
    Log.d("SecondActivity","$user")
    Log.d("SecondActivity","$beike")
  }






}