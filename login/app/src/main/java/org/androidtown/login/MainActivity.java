package org.androidtown.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

boolean isPageOpen=false;
    Animation transleft;
    Animation transright;

    RelativeLayout slidingPage01;
    RelativeLayout slidingPage02;
    Button button1;
    String result;
    String loginID;
    String loginPassword;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    private EditText editid;
    private EditText editpassword;
    private EditText editname;
    private EditText editdepartment;
    private EditText editrank;
    private EditText editText;
    private EditText editText2;
    private CheckBox checkBox;

    String myJSON;
    JSONArray peoples = null;
    private static final String TAG_RESULTS="result";
    private static final String TAG_END="end";
    ArrayList<HashMap<String, String>> personList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingPage01=(RelativeLayout)findViewById(R.id.slidingPage01);
        slidingPage02=(RelativeLayout)findViewById(R.id.asdf);
        button1=(Button)findViewById(R.id.button1);

        transleft= AnimationUtils.loadAnimation(this,R.anim.translate_left);
        transright=AnimationUtils.loadAnimation(this,R.anim.translate_right);

        SlidingPageAnimationListener animationListener=new SlidingPageAnimationListener();
        transleft.setAnimationListener(animationListener);
        transright.setAnimationListener(animationListener);

        editid=(EditText)findViewById(R.id.editid);
        editpassword=(EditText)findViewById(R.id.editpassword);
        editname=(EditText)findViewById(R.id.editname);
        editdepartment=(EditText)findViewById(R.id.editdepartment);
        editrank=(EditText)findViewById(R.id.editrank);
        editText=(EditText)findViewById(R.id.edittext);
        editText2=(EditText)findViewById(R.id.edittext2);
        checkBox=(CheckBox)findViewById(R.id.checkbox);

        setting=getSharedPreferences("setting",0);
        editor=setting.edit();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked())
                {
                    loginID=editText.getText().toString();
                    loginPassword=editText2.getText().toString();

                    editor.putString("ID",loginID);
                    editor.putString("PW",loginPassword);
                    editor.putBoolean("Auto_Login_enabled",true);
                    editor.commit();
                }
                else{
                    editor.remove("ID");
                    editor.remove("PW");
                    editor.remove("Auto_Login_enabled");
                    editor.clear();
                    editor.commit();
                }
            }
        });
        if(setting.getBoolean("Auto_Login_enabled",false)){
            editText.setText(setting.getString("ID",""));
            editText2.setText(setting.getString("PW",""));
            checkBox.setChecked(true);
            Intent intent = new Intent(this, administrator.class);
            startActivity(intent);
            finish();
            Toast.makeText(this,"자동 로그인",Toast.LENGTH_SHORT).show();
        }


    }
    public void but1click(View v){
        String ID=editid.getText().toString();
        String Password=editpassword.getText().toString();
        String Name=editname.getText().toString();
        String Department=editdepartment.getText().toString();
        String Rank=editrank.getText().toString();

        if(!ID.isEmpty()&&!Password.isEmpty()&&!Name.isEmpty()) {
            insertToDatabase(ID, Password, Name, Department, Rank);
            editid.setText("");
            editpassword.setText("");
            editname.setText("");
            editdepartment.setText("");
            editrank.setText("");
        }
        else
            Toast.makeText(this,"필수항목을 입력해주세요",Toast.LENGTH_SHORT).show();
    }

    private void insertToDatabase(String ID, String Password,String Name,String Department,String Rank){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String ID = (String)params[0];
                    String Password = (String)params[1];
                    String Name = (String)params[2];
                    String Department = (String)params[3];
                    String Rank = (String)params[4];

                    String link="http://192.168.123.103/info.php";
                    String data  = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");
                    data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");
                    data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("Department", "UTF-8") + "=" + URLEncoder.encode(Department, "UTF-8");
                    data += "&" + URLEncoder.encode("Rank", "UTF-8") + "=" + URLEncoder.encode(Rank, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
       task.execute(ID,Password,Name,Department,Rank);
    }

    private void LoadToDatabase(String ID, String Password){

        class LoadToData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String ID = (String)params[0];
                    String Password = (String)params[1];


                    String link="http://192.168.123.103/load.php";
                    String data  = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");
                    data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");


                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;


                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                    result=sb.toString();
                    Log.e("log_tag", "result:"+result);



                    return sb.toString();

                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());

                }
            }
        }

        LoadToData task = new LoadToData();
        task.execute(ID,Password);
    }




   public void Click1(View v){
       String ID=editText.getText().toString();
       String Password=editText2.getText().toString();

       if(!ID.isEmpty()&&!Password.isEmpty())
       LoadToDatabase(ID,Password);
       else
       Toast.makeText(this,"모두 입력해주세요",Toast.LENGTH_SHORT).show();

       this.sleep(2000);

           if (result != null) {
               if (result.equalsIgnoreCase("로그인성공")) {
                   Intent intent = new Intent(this, administrator.class);
                   startActivity(intent);
                   finish();
               }
               else
                   Toast.makeText(MainActivity.this, "로그인실패", Toast.LENGTH_LONG).show();
           }


   }
    public void Click2(View v){
        if(isPageOpen){
            slidingPage01.startAnimation(transright);
        slidingPage02.setVisibility(View.VISIBLE);}
        else{
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage02.setVisibility(View.GONE);
            slidingPage01.startAnimation(transleft);
        }
    }
    public void onButton1Clicked(View v) {
        if (isPageOpen) {
            slidingPage01.startAnimation(transright);
            slidingPage02.setVisibility(View.VISIBLE);
        } else {
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage02.setVisibility(View.GONE);
            slidingPage01.startAnimation(transleft);
        }
    }
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation){
            if(isPageOpen){
                slidingPage01.setVisibility(View.GONE);
                slidingPage02.setVisibility(View.VISIBLE);
                button1.setVisibility(View.GONE);
                isPageOpen=false;
            }
            else{
                button1.setVisibility(View.VISIBLE);
                button1.setText("뒤로가기");
                isPageOpen=true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) { }
    }

    public void slidingPage01click (View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(slidingPage02.getWindowToken(), 0);
    }


    /*public void getData(String url){
               class GetDataJSON extends AsyncTask<String, Void, String>{

                              @Override
                        protected String doInBackground(String... params) {
                                  String uri = params[0];
                                  BufferedReader bufferedReader = null;
                              try {
                                        URL url = new URL(uri);
                                       HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                     StringBuilder sb = new StringBuilder();

                                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                                     String json;
                                      while((json = bufferedReader.readLine())!= null){
                                             sb.append(json+"\n");
                                           }
                                     String abc=sb.toString();
                                     if("success".equalsIgnoreCase(sb.toString()))
                                         Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                                         else
                                         Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();

                                       return sb.toString().trim();

                                   }catch(Exception e){
                                     return null;
                                 }
                       }
                             @Override
                      protected void onPostExecute(String result){
                              myJSON=result;
                         }
                 }
               GetDataJSON g = new GetDataJSON();
               g.execute(url);
            }*/
}
