package com.hyc.parrot;

import android.os.Bundle;
import android.util.Log;
import com.hyc.parrot.init.InitClassParam;
import com.hyc.parrot.init.InitParam;
import java.io.IOException;
import kotlin.jvm.Throws;
import org.jetbrains.annotations.Nullable;

/**
 * @author: 贺宇成
 * @date: 2019-12-10 15:40
 * @desc:
 */
public class ThreeActivity extends BaseActivity {

  @InitParam("int")
  private int i = 1;
  @InitParam("long")
  private long l =2;
  @InitParam("double")
  private double d=3;
  @InitParam("float")
  private float f = 8.0f;
  @InitParam("string")
  private String s = "str";
  private UserBean userBean;
  private int intString = 4;
  private long longString = 5;
  private double doubleString = 6;
  private float floatString = 7.0f;
  @InitParam("jsonObject")
  private UserBean user;
  @InitClassParam
  private Student student;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    printParam();
  }

  private void printParam(){
    Log.d("ThreeActivity", String.valueOf(i));
    Log.d("ThreeActivity", String.valueOf(l));
    Log.d("ThreeActivity", String.valueOf(d));
    Log.d("ThreeActivity", String.valueOf(f));
    Log.d("ThreeActivity", s);
    Log.d("ThreeActivity",userBean.toString());
    Log.d("ThreeActivity", String.valueOf(intString));
    Log.d("ThreeActivity", String.valueOf(longString));
    Log.d("ThreeActivity", String.valueOf(doubleString));
    Log.d("ThreeActivity", String.valueOf(floatString));
    Log.d("ThreeActivity",user.toString());
    Log.d("ThreeActivity",student.toString());
  }




}
