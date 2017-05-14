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

    String ip="http://192.168.123.100/";

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

    String compare;
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
            if(editText.getText().toString().equals("admin")) {
                Intent intent = new Intent(this, administrator.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(this, user.class);
                intent.putExtra("ID", editText.getText().toString());
                intent.putExtra("PW", editText2.getText().toString());
                startActivity(intent);
                finish();
                Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show();
            }
        }


    }
    public void but1click(View v){
        String ID=editid.getText().toString();
        String Password=editpassword.getText().toString();
        String Name=editname.getText().toString();
        String Department=editdepartment.getText().toString();
        String Rank=editrank.getText().toString();
        String address=ip+"signup.php";
if(ID.length()<=10&&Password.length()<=10)
        if(!ID.isEmpty()&&!Password.isEmpty()&&!Name.isEmpty()&&!Department.isEmpty()&&!Rank.isEmpty()&&editid.getText().toString().equals(compare)) {
            LoadToDatabase(ID, Password, Name, Department, Rank,"0",address);
            editid.setText("");
            editpassword.setText("");
            editname.setText("");
            editdepartment.setText("");
            editrank.setText("");
            slidingPage02.setVisibility(View.GONE);
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage01.startAnimation(transleft);
            Toast.makeText(this,"회원가입 요청완료",Toast.LENGTH_SHORT).show();
        }
        else if(!editid.getText().toString().equals(compare))
            Toast.makeText(this,"아이디 중복확인을 해주세요",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"모든항목을 입력해주세요",Toast.LENGTH_SHORT).show();
    }

    private void LoadToDatabase(String ID, String Password, String Name, String Department, String Rank, String AC, final String address){

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

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String ID = (String)params[0];
                    String Password = (String)params[1];
                    String Name = (String)params[2];
                    String Department = (String)params[3];
                    String Rank = (String)params[4];
                    String AC=(String)params[5];


                    String link=address;
                    String data  = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");
                    data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");
                    data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("Department", "UTF-8") + "=" + URLEncoder.encode(Department, "UTF-8");
                    data += "&" + URLEncoder.encode("Rank", "UTF-8") + "=" + URLEncoder.encode(Rank, "UTF-8");
                    data += "&" + URLEncoder.encode("AC", "UTF-8") + "=" + URLEncoder.encode(AC, "UTF-8");


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
        task.execute(ID,Password,Name,Department,Rank,AC);
    }





    public void Click1(View v){
       String ID=editText.getText().toString();
       String Password=editText2.getText().toString();
        String address1 = ip + "admin.php";
        String address2 = ip + "load.php";
       if(!ID.isEmpty()&&!Password.isEmpty())
       {
           if (ID.equals("admin"))
               LoadToDatabase(ID, Password, "0", "0", "0", "0", address1);
           else
               LoadToDatabase(ID, Password, "0", "0", "0", "0", address2);
       }
       else
       Toast.makeText(this,"모두 입력해주세요",Toast.LENGTH_SHORT).show();

       this.sleep(1500);

           if (result != null) {
               if(ID.equals("admin")&&result.equals("관리자로그인"))
               {
                   Intent intent = new Intent(this, administrator.class);
                   Toast.makeText(MainActivity.this, "관리자로그인", Toast.LENGTH_SHORT).show();
                   startActivity(intent);
                   finish();
               }
               else if (result.equalsIgnoreCase("로그인성공")) {
                   Intent intent = new Intent(this, user.class);
                   intent.putExtra("ID", ID);
                   intent.putExtra("PW", Password);
                   Toast.makeText(MainActivity.this, "로그인성공", Toast.LENGTH_SHORT).show();
                   startActivity(intent);
                   finish();
               }
               else
                   Toast.makeText(MainActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();
           }


   }
    public void Click2(View v){
        if(isPageOpen){
            slidingPage01.startAnimation(transright);
            slidingPage02.setVisibility(View.VISIBLE);
            }
        else{
            slidingPage02.setVisibility(View.GONE);
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage01.startAnimation(transleft);
        }
    }
    public void onButton1Clicked(View v) {
        if (isPageOpen) {
            slidingPage01.startAnimation(transright);
            slidingPage01.setVisibility(View.GONE);
            slidingPage02.setVisibility(View.VISIBLE);
        } else {
            slidingPage02.setVisibility(View.GONE);
            slidingPage01.setVisibility(View.VISIBLE);
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
                button1.setVisibility(View.GONE);
                slidingPage02.setVisibility(View.VISIBLE);
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

    public void sameclick(View v){
        String ID=editid.getText().toString();
        String address=ip+"same.php";
        LoadToDatabase(ID,"0","0","0","0","0",address);
        sleep(1500);
        if(result.equals("사용 가능합니다.")&&ID.length()<=10) {
            Toast.makeText(this,"사용 가능합니다.",Toast.LENGTH_SHORT).show();
            compare = editid.getText().toString();
        }
        else
            Toast.makeText(this,"사용 불가능합니다.",Toast.LENGTH_SHORT).show();
    }

}
