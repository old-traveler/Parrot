package com.hyc.parrot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.hyc.parrot_lib.InitCache;
import com.hyc.parrot_lib.InitClassParam;
import com.hyc.parrot_lib.InitDataStructure;
import com.hyc.parrot_lib.InitParam;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  @InitDataStructure({"str1","str2"})
  public String[] strings;
  @InitDataStructure({"int","long","double","longString","floatString"})
  private Bundle bundle;
  @InitDataStructure({"int","long","double","longString","floatString"})
  private Map map;
  @InitDataStructure({"int","long","double","longString","floatString"})
  private Set set;
  @InitDataStructure({"int","long","double","longString","floatString"})
  private List list;
  @InitDataStructure({"int","long","double","longString","floatString"})
  private int[] intArray;
  @InitDataStructure({"int","intString"})
  private int[] intArray1;

  private String prefix;

  @InitCache(value = "clickCount",prefixField = "prefix")
  private int clickCount = 0;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    prefix = this.getClass().getSimpleName();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    printParam();
    final TextView tvTitle = findViewById(R.id.tv_title);
    tvTitle.setText("共计点击了多少次："+clickCount);
    tvTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        tvTitle.setText("共计点击了多少次："+(++clickCount));
      }
    });

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
    for (String string : strings) {
      Log.d("ThreeActivity","数组:"+string);
    }
    Log.d("ThreeActivity",""+map.size());
    Log.d("ThreeActivity",""+bundle.keySet().size());
    Log.d("ThreeActivity",""+set.size());
    Log.d("ThreeActivity",""+list.size());
    Log.d("ThreeActivity",""+intArray.length);
    for (int i1 : intArray) {
      Log.d("ThreeActivity","intArray1  "+i1);
    }
  }

}
