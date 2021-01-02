package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {
    public static String LOGINBACKTOKEN="http://47.115.93.213:8080/admin/login";
    private static TextView tv;
    private TextView top2;
    public static Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            tv.setText((String)msg.obj);
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        setContentView(R.layout.activity_main);
        final EditText user=(EditText)findViewById(R.id.name);
        final EditText pass=(EditText)findViewById(R.id.password);
        tv=(TextView)findViewById(R.id.test);
        // 监听事件监听登陆按钮
        top2=(Button)findViewById(R.id.login_button);
        top2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                new Thread(new Runnable() {

                    @Override
                    public void run() {
// TODO Auto-generated method stub
                            loginByPost(user.getText().toString(), pass.getText().toString());
                            //测试帐号123  密码123
                    }
                }).start();
            }
        });
    }

    public static String loginByPost(String username, String password){
        try {

            URL url = new URL(LOGINBACKTOKEN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            // Post请求不能使用缓存
            conn.setUseCaches(false);
               /* String data = "username="+ URLEncoder.encode(username)+"&password="
                        +URLEncoder.encode(password);*/
            conn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            conn.setRequestProperty("Content-Type",
                    "application/json");
            conn.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false
            //设置body内的参数，put到JSONObject中
            JSONObject param = new JSONObject();
            param.put("username", username);
            param.put("password", password);
            // 建立实际的连接
            conn.connect();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            writer.write(param.toString());
            writer.flush();
            int code = conn.getResponseCode();
            System.out.println(conn);
            System.out.println(code);
            if (code == 200) {
                // 获取服务端响应，通过输入流来读取URL的响应
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                // 关闭连接
                conn.disconnect();
                // 打印读到的响应结果
                System.out.println("运行结束："+sbf.toString());
                String text = sbf.toString();
                Message msg=new Message();
                msg.obj=text;
                // 只需将返回信息的msg传给前端
                JSONObject jsonObject = new JSONObject(text);
                String message = jsonObject.getString("message");
                msg.obj = message;
                mHandler.sendMessage(msg);
                return text;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("111111");
        } catch (ProtocolException e) {
            System.out.println("222222");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("33333");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
