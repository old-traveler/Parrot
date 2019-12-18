# Parrot
自动将Bundle数据注入页面参数




## 使用 

init JsonConvert in Application
```kotlin
class MyJsonConvert : JsonConvert {
  private val mGson : Gson = Gson()

  override fun <T> fromJson(json: String, classOfT: Class<T>): T? {
    return mGson.fromJson(json,classOfT)
  }

  override fun toJson(src: Any): String? {
    return mGson.toJson(src)
  }
}

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Parrot.initJsonConvert(MyJsonConvert())
  }
}

```

in Activity or Fragment

```kotlin
open class BaseActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Parrot.initParam(this)
  }
}

```

in start Activity

```kotlin
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
```

in receive activity

```kotlin

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
  
```
