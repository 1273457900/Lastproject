package ncist.wl171.no201701024118.lastproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;


import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

/*
    登录（LoginActivity）成功后进入好友页面HomeAtivity
 */
public class HomeActivity extends AppCompatActivity {

    static String SERVICE_ROOT_URL = "http://182.92.71.44:7999/LoginServlet/"; //要与LoginActivity里一致！
    static String SERVICE_UAP_URL = SERVICE_ROOT_URL + "images/UAP/"; //服务器用户头像路径
    static String SERVICE_ROOT_URL2 = "http://182.92.71.44:7999/LoginServlet2/"; //项目的根路径

    int status; //用户登录结果码
    User user; //个人用户信息
    User se_user;
    static final String TAG = "测试";
    String username, password;
    EditText se_username;
    ImageView btn_add;
    Button btn_search;
    Button btn_addfriends;
    ArrayList<User> friendsList;
    ArrayList<User> friendList;
    final List<String> components = new ArrayList<>();

    //Handler对象，用于接收“登录子线程”发送过来的消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String toastText = "";
            switch (msg.what) {
                case 5:
                    toastText = "搜索成功";
                    break;
                case -1:
                    toastText = "用户名不存在";
                    break;


                default:
                    toastText = "未知错误（如Web服务器未启动或URL错误等）";
                    break;
            }
            Toast.makeText(HomeActivity.this, toastText, Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        getSupportActionBar().hide();

        friendsList = getIntent().getParcelableArrayListExtra("friendsList");
        myBaseAdapter adapter = new myBaseAdapter();
        ListView lv_friends = findViewById(R.id.lv_friends);
        lv_friends.setAdapter(adapter);

    }


    public void coffe(View view) {
        Intent intent = new Intent(HomeActivity.this, TopLevelActivity.class);
        startActivity(intent);
    }

    public void friends(View view) {
        System.out.println("点击了+");
        init();

    }

   /* public void news(View view) {
        Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
        startActivity(intent);
    }*/


    class myBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return friendsList.size();
        }

        @Override
        public Object getItem(int position) {
            return friendsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.home_view_listitem, null);

            TextView tv_username = view.findViewById(R.id.tv_username);
            ImageView iv_ico = view.findViewById(R.id.iv_ico);
            User user = (User) getItem(position);
            tv_username.setText(user.getUsername());
            iv_ico.setImageResource(R.drawable.sun);
            long userID;
            userID=user.getUserID();
            userID=userID%4;
            String imageUrl = SERVICE_UAP_URL + userID + ".png";
            Log.d("测试", "imageUrl:" + imageUrl);
            Glide.with(HomeActivity.this)
                    .load(imageUrl)
                    .error(R.drawable.sun)  //任选
                    .into(iv_ico);
            return view;
        }
    }
    class myBaseAdapter2 extends BaseAdapter {
        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Object getItem(int position) {
            return friendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.home_view_listitem, null);

            TextView tv_username = view.findViewById(R.id.tv_username);
            ImageView iv_ico = view.findViewById(R.id.iv_ico);
            User user = (User) getItem(position);
            tv_username.setText(user.getUsername());
            iv_ico.setImageResource(R.drawable.sun);

            long userID;
            userID=user.getUserID();
            userID=userID%4;
            String imageUrl = SERVICE_UAP_URL + userID + ".png";
            Log.d("测试", "imageUrl:" + imageUrl);
            Glide.with(HomeActivity.this)
                    .load(imageUrl)
                    .error(R.drawable.sun)  //任选
                    .into(iv_ico);
            return view;
        }
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认退出吗？")
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("注销", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences("login", Context.MODE_PRIVATE).edit().remove("jsonString").commit(); //删除jsonString记录
                        Intent inttent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(inttent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void init() {


        // iv_UAP = findViewById(R.id.iv_UAP);
        //se_username = findViewById(R.id.search_username);
        //et_username= findViewById(R.id.et_username);
        //  et_password= findViewById(R.id.et_password);
        // btn_login = findViewById(R.id.btn_login);
        btn_add = findViewById(R.id.imageView2);
        // btn_register = findViewById(R.id.btn_register);

        btn_add.setOnClickListener(new View.OnClickListener() {  //搜索  加号按钮
            @Override
            public void onClick(View v) {
                final View view = View.inflate(HomeActivity.this, R.layout.add_view, null);
                  se_username=view.findViewById(R.id.search_username);
                 btn_search = view.findViewById(R.id.search);
                btn_addfriends = view.findViewById(R.id.add);


                btn_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//搜索按钮
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {

                                    //调用register()方法进行登录操作

                                    int result = search(view);
                                    Message message = new Message();
                                    System.out.println(result);
                                    switch (result) {
                                        case 5:
                                            //case REGISTER_SUCCESS :
                                            Log.d(TAG, "搜索成功");
                                            message.what = 5;
                                            handler.sendMessage(message);



                                            break;

                                        case -1:
                                            Log.d(TAG, "用户不存在");
                                            message.what = -1;
                                            handler.sendMessage(message);
                                            break;


                                        default:
                                            Log.d(TAG, "其它错误");
                                            break;
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "Exception:" + e.getMessage());
                                }
                            }
                        }).start();

                        //  Toast.makeText(LoginActivity.this, "请完善注册功能", Toast.LENGTH_SHORT).show();

                    }
                });


                new android.app.AlertDialog.Builder(HomeActivity.this)
                        .setView(view)
                        .show();

            }
        });



    }

    private int search(final View view) {


        se_username =view.findViewById(R.id.search_username);
        System.out.println("se_username=" + se_username);
        //   EditText re_passname = view.findViewById(R.id.re_password);
        username = se_username.getText().toString();  //用户信息

        System.out.println("username=" + username);
        //   password = re_passname.getText().toString();
        String loginUrl = SERVICE_ROOT_URL2 + "isUserExsit";  //用户搜索URL
        Response.Listener listener = new Response.Listener<String>() {  //成功得到服务器响应时的监听器
            @Override
            public void onResponse(String s) {
                //  sharedPreferences.edit().putString("jsonString",s).commit(); //将接送字符串保存起来，下次直接登录
                parseJsonString(s,view);  //解析从服务器获取到的JSON字符串，给类成员赋值
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() { //未成功得到服务器响应时的监听器
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "ErrorListener:" + volleyError.getMessage());
                Toast.makeText(HomeActivity.this, "服务器正在维护中...", Toast.LENGTH_SHORT).show();
            }
        };
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, listener, errorListener) {  //创建StringRequest对象
            @Override
            protected Map<String, String> getParams() { //重写方法，POST方式传参
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                // map.put("password",password);
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

    private void parseJsonString(String s,View view) {  //解析从服务器获取到的JSON字符串，给类成员赋值
        try {
            //将JSON字符串转化为JSON对象
            JSONObject resultObject = new JSONObject(s);
            status = resultObject.getInt("status"); //设置用户的登录状态
            //设置用户
            JSONObject userObject = resultObject.getJSONObject("user");
            //设置搜索用户
           // user = new User(userObject.getInt("userID"), userObject.getString("username"));
            friendList = new ArrayList<User>();
                    long userID = userObject.getLong("userID");
                    String username = userObject.getString("username");
                    User user = new User(userID, username);
            friendList.add(user);

            myBaseAdapter2 adapter = new myBaseAdapter2();
            ListView lv_friend = view.findViewById(R.id.search_friend);
            lv_friend.setAdapter(adapter);






//            //设置该用户的好友
//            JSONArray friendsArray = resultObject.getJSONArray("friends");
//
//            friendsList = new ArrayList<User>();
//            if (friendsArray.length() > 0) {
//                for (int i = 0; i < friendsArray.length(); i++) {
//                    JSONObject friendObject = friendsArray.getJSONObject(i);
//                    long userID = friendObject.getLong("userID");
//                    String username = friendObject.getString("username");
//                    User user = new User(userID, username);
//                    friendsList.add(user);
//                }
//
//            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException:" + e.getMessage());
        }

    }







}

