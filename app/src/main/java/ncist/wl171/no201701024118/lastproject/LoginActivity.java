package ncist.wl171.no201701024118.lastproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
    本模块模仿QQ登录，需要访问Web服务器。运行本程序前，要求手机能访问Web服务器
    为使Web服务器与手机在同一网段内，可以让电脑使用手机热点上网
    在命令行方式下，Web服务器IP使用ipconfig/all查看
    LoginActivity是界面程序，用于用户登录，其用户名及密码来源于数据库
    本模块同时使用了网络编程框架Volley 2015.05.28和Glide 3.7.0
    登录成功后，将进入好友页面HomeAtivity
    注册功能，由读者模仿完成。
 */
public class LoginActivity extends AppCompatActivity {
    static final int LOGIN_SUCCESS = 1;
    static final int USER_NOT_EXIST = -1;
    static final int PASSWORD_INCORRECT = -2;
    static final int REGISTER_SUCCESS = 3;
    static final int REGISTER_FAIL = 4;
    static final int USER_IS_EXIST = 5;

    static String SERVICE_ROOT_URL = "http://182.92.71.44:7999/LoginServlet/"; //项目的根路径
    static String SERVICE_UAP_URL = SERVICE_ROOT_URL + "images/UAP/"; //服务器用户头像路径
    static final String TAG = "测试";

  //  EditText re_username ;
  //  EditText re_password ;



    ImageView iv_UAP;
    EditText et_username;
    EditText et_password;
    Button btn_login;
    Button btn_register;
    SharedPreferences sharedPreferences;
    User user; //个人用户信息
    String username,password;
    int status; //用户登录结果码
    ArrayList<User> friendsList; //好友用户信息集合
    String[] visitedList;  //在用户名文本框下显示使用共享存储的已登录过的用户名
    ListPopupWindow listPopupWindow;  //实现文本框的选择输入

    //Handler对象，用于接收“登录子线程”发送过来的消息
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String toastText = "";
            switch (msg.what){
                case LOGIN_SUCCESS:
                    toastText = "登录成功"; break;
                case USER_NOT_EXIST:
                    toastText = "用户名不存在"; break;
                case PASSWORD_INCORRECT:
                    toastText = "密码错误"; break;
                case REGISTER_SUCCESS:
                    toastText = "注册成功";break;
                case REGISTER_FAIL:
                    toastText = "注册失败";break;
                case USER_IS_EXIST:
                    toastText = "用户已存在";break;



                default:toastText = "未知错误（如Web服务器未启动或URL错误等）"; break;
            }
            Toast.makeText(LoginActivity.this, toastText, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String jsonString;
        if ((jsonString = sharedPreferences.getString("jsonString","empty")).equals("empty")){
            init(); //如果是第一次登录，执行登录界面操作
        }else {
            //自定义方法，将JSON字符串转为JavaScript对象后，给类成员赋值
            parseJsonString(jsonString);
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            /*带列表数据，在组件调用时要把Lis数据传给下一个组件，此时只需要定义一个bundle，然后使用bundle.putParcelableArrayList()即可。
             但如果这个类型中包含其他自定义类型（实体类）的List,目标组件可能接收的可能空。事实上，定义实体类需要实现序列化接口Parcelable
             */
            intent.putParcelableArrayListExtra("friendsList",friendsList);
            startActivity(intent);
            finish();  //销毁当前的Activity
        }
    }
    private void init() {



        iv_UAP = findViewById(R.id.iv_UAP);
        et_username= findViewById(R.id.et_username);
        et_password= findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_login.setOnClickListener(new View.OnClickListener() {  //登录
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //调用login()方法进行登录操作
                            int result = login();
                            Message message = new Message();
                            System.out.println(result);
                            switch (result){
                                //case 1:
                                case LOGIN_SUCCESS :
                                    Log.d(TAG,"登录成功");
                                    sharedPreferences.edit().putLong(user.getUsername(),user.getUserID()).commit();
                                    message.what=LOGIN_SUCCESS;
                                    handler.sendMessage(message);
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    //对象序列化后传输
                                    intent.putParcelableArrayListExtra("friendsList",friendsList);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case USER_NOT_EXIST:
                                    //case -1:
                                    Log.d(TAG,"用户不存在");
                                    message.what=USER_NOT_EXIST;
                                    handler.sendMessage(message);
                                    break;
                                case PASSWORD_INCORRECT:
                                    //case -2:
                                    Log.d(TAG,"密码错误");
                                    message.what=PASSWORD_INCORRECT;
                                    handler.sendMessage(message);
                                    break;
                                default:
                                    Log.d(TAG, "其它错误");
                                    break;
                            }
                        }catch (Exception e){
                            Log.d(TAG,"Exception:"+e.getMessage());
                        }
                    }
                }).start();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {  //注册 注册 注册 注册 注册
            @Override
            public void onClick(View v) {
                final View view = View.inflate(LoginActivity.this,R.layout.register_view,null);

                Button btn_register_start = view.findViewById(R.id.btn_register_start);



                btn_register_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try{
                                //调用register()方法进行登录操作

                                int result = register(view);
                                Message message = new Message();
                                System.out.println(result);
                                switch (result){
                                    case 3:
                                    //case REGISTER_SUCCESS :
                                        Log.d(TAG,"注册成功");
                                        message.what=REGISTER_SUCCESS;
                                        handler.sendMessage(message);

                                       /* sharedPreferences.edit().putLong(user.getUsername(),user.getUserID()).commit();

                                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                        //对象序列化后传输
                                        intent.putParcelableArrayListExtra("friendsList",friendsList);
                                        startActivity(intent);
                                        finish();*/
                                        break;
                                    case 1:
                                        //case -1:
                                        Log.d(TAG,"用户已存在");
                                        message.what=USER_IS_EXIST;
                                        handler.sendMessage(message);
                                        break;

                                    //case REGISTER_FAIL:
                                        case 4:
                                        Log.d(TAG,"注册失败");
                                        message.what=REGISTER_FAIL;
                                        handler.sendMessage(message);
                                        break;
                                    default:
                                        Log.d(TAG, "其它错误");
                                        break;
                                }
                            }catch (Exception e){
                                Log.d(TAG,"Exception:"+e.getMessage());
                            }
                        }
                    }).start();



                      //  Toast.makeText(LoginActivity.this, "请完善注册功能", Toast.LENGTH_SHORT).show();



                    }
                });





                new AlertDialog.Builder(LoginActivity.this)
                        .setView(view)
                        .show();
                btn_register_start.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {    //隐藏功能：长按后修改服务器IP
                        String ipAddress = et_username.getText().toString();
                        Toast.makeText(LoginActivity.this, "原服务器地址\n"+SERVICE_ROOT_URL, Toast.LENGTH_LONG).show();
                        LoginActivity.SERVICE_ROOT_URL = "http://"+ipAddress+":8080/LoginServlet/";
                        LoginActivity.SERVICE_UAP_URL = SERVICE_ROOT_URL + "images/UAP/";
                        HomeActivity.SERVICE_ROOT_URL = "http://"+ipAddress+":8080/LoginServlet/";
                        HomeActivity.SERVICE_UAP_URL = SERVICE_ROOT_URL + "images/UAP/";
                        Toast.makeText(LoginActivity.this, "新服务器地址\n"+SERVICE_ROOT_URL, Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }
        });

        //两个编辑文本框的监听器，接口TextWatcher包含3个要实现的方法
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                //输入了用户名密码才允许登录
                if(!("".equals(et_username.getText().toString())) && !("".equals(et_password.getText().toString())) ){
                    btn_login.setBackgroundColor(getResources().getColor(R.color.colorBtn2));
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setBackgroundColor(getResources().getColor(R.color.colorBtn1));
                    btn_login.setEnabled(false);
                }
                //如果用户登录过，就在用户头像区域加载该用户头像
                long userID;
                Map<String, ?> map = sharedPreferences.getAll();
                if(map.containsKey(et_username.getText().toString())){
                    userID = (Long) (map.get(et_username.getText().toString()));
                    String imageUrl = SERVICE_UAP_URL +userID+".png";
                    //使用Android图片加载框架Glide 3.7.0
                    Glide.with(LoginActivity.this)
                            .load(imageUrl)  //加载网络资源
                            .error(R.drawable.sun)  //任选
                            .into(iv_UAP);  //在控件是显示
                }else{
                    iv_UAP.setImageResource(R.drawable.sun); //加载本地资源
                }
            }
        };
        et_username.addTextChangedListener(textWatcher);
        et_password.addTextChangedListener(textWatcher);

        //用户名文本框辅助输入：为用户名输入框右边的那个三角图片设置了监听器
        et_username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = et_username.getCompoundDrawables()[2];
                //如果右边没有三角形图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理（onTouch包含按下手指和抬起手指两个动作，两个动作执行完毕就是一个onClick）
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                //按下的区域是否在三角形图片区域内
                if (event.getX() > et_username.getWidth() - et_username.getPaddingRight() - drawable.getIntrinsicWidth()){
                    showListPopulWindow();
                }
                return false;
            }
        });
    }



    void showListPopulWindow(){
        Set<String> usernameSet = sharedPreferences.getAll().keySet();
        visitedList = new String[usernameSet.size()];
        int i = 0;
        for (String username:usernameSet) {
            visitedList[i++] = username;
        }
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,visitedList));
        listPopupWindow.setAnchorView(et_username);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_username.setText(visitedList[position]);
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }

    private int login(){  //用户登录，使用网络通信框架Volley，返回登录状态码
        username = et_username.getText().toString();  //登录信息
        password = et_password.getText().toString();
        String loginUrl = SERVICE_ROOT_URL + "Login";  //用户登录URL
        Response.Listener listener = new Response.Listener<String>() {  //成功得到服务器响应时的监听器
            @Override
            public void onResponse(String s) {
                sharedPreferences.edit().putString("jsonString",s).commit(); //将接送字符串保存起来，下次直接登录
                parseJsonString(s);  //解析从服务器获取到的JSON字符串，给类成员赋值
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() { //未成功得到服务器响应时的监听器
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "ErrorListener:"+volleyError.getMessage());
                Toast.makeText(LoginActivity.this, "服务器正在维护中...", Toast.LENGTH_SHORT).show();
            }
        };
        StringRequest stringRequest= new StringRequest(Request.Method.POST,loginUrl,listener,errorListener){  //创建StringRequest对象
            @Override
            protected Map<String, String> getParams() { //重写方法，POST方式传参
                Map<String, String> map = new HashMap<>();
                map.put("username",username);
                map.put("password",password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);//加入请求至队列
        try {
            Thread.sleep(1000);  // 休眠1秒，等待status值更新
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }



    private int register(View view){  //用户注册，使用网络通信框架Volley，返回登录状态码

        EditText re_username = view.findViewById(R.id.re_username);
        EditText re_passname = view.findViewById(R.id.re_password);
        username = re_username.getText().toString();  //注册信息
        password = re_passname.getText().toString();
        String loginUrl = SERVICE_ROOT_URL + "register";  //用户注册URL
        Response.Listener listener = new Response.Listener<String>() {  //成功得到服务器响应时的监听器
            @Override
            public void onResponse(String s) {
                // sharedPreferences.edit().putString("jsonString",s).commit(); //将接送字符串保存起来，下次直接登录
                parseJsonString(s);  //解析从服务器获取到的JSON字符串，给类成员赋值
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() { //未成功得到服务器响应时的监听器
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "ErrorListener:"+volleyError.getMessage());
                Toast.makeText(LoginActivity.this, "服务器正在维护中...", Toast.LENGTH_SHORT).show();
            }
        };
        StringRequest stringRequest= new StringRequest(Request.Method.POST,loginUrl,listener,errorListener){  //创建StringRequest对象
            @Override
            protected Map<String, String> getParams() { //重写方法，POST方式传参
                Map<String, String> map = new HashMap<>();
                map.put("username",username);
                map.put("password",password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);//加入请求至队列
        try {
            Thread.sleep(100);  // 休眠1秒，等待status值更新
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return status;
    }



    private void parseJsonString(String s) {  //解析从服务器获取到的JSON字符串，给类成员赋值
        try {
            //将JSON字符串转化为JSON对象
            JSONObject resultObject = new JSONObject(s);
            status = resultObject.getInt("status"); //设置用户的登录状态
            //设置用户
            JSONObject userObject = resultObject.getJSONObject("user");
            user = new User(userObject.getInt("userID"), userObject.getString("username"));
            //设置该用户的好友
            JSONArray friendsArray = resultObject.getJSONArray("friends");

            friendsList = new ArrayList<User>();
            if (friendsArray.length() > 0) {
                for (int i = 0; i < friendsArray.length(); i++) {
                    JSONObject friendObject = friendsArray.getJSONObject(i);
                    long userID = friendObject.getLong("userID");
                    String username = friendObject.getString("username");
                    User user = new User(userID, username);
                    friendsList.add(user);
                }

            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException:" + e.getMessage());
        }

    }
}
