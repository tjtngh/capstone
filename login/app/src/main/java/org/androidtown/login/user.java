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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by WBHS on 2017-05-08.
 */
public class user extends AppCompatActivity {


    SharedPreferences setting;
    SharedPreferences.Editor editor;

    RelativeLayout page1,page2,page3;

    TextView text1,text2;
    EditText edit1,edit2,edit3,pw1,pw2,pw3;
    String ID,Password,result;
    String ip="http://192.168.123.100/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        Password=intent.getStringExtra("PW");

        page1=(RelativeLayout)findViewById(R.id.page1);
        page2=(RelativeLayout)findViewById(R.id.page2);
        page3=(RelativeLayout)findViewById(R.id.page3);

        text1=(TextView)findViewById(R.id.teid);
        text2=(TextView)findViewById(R.id.teac);
        edit1=(EditText)findViewById(R.id.edname);
        edit2=(EditText)findViewById(R.id.eddepart);
        edit3=(EditText)findViewById(R.id.edrank);

        pw1=(EditText)findViewById(R.id.pw1ed);
        pw2=(EditText)findViewById(R.id.pw2ed);
        pw3=(EditText)findViewById(R.id.pw3ed);
  }

    public void adclick1(View v) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        setting=getSharedPreferences("setting",0);
        editor=setting.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this,"로그아웃",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void btninfo(View v){
        String address=ip+"setdata3.php";
        String setdata[];


        if(!text1.getText().toString().equals(ID)) {
            LoadToDatabase(ID, "0", "0", "0", "0", "0", address);
            sleep(2000);
            if (result != null) {
                setdata = result.split("-");
                text1.setText(setdata[0]);
                edit1.setText(setdata[2]);
                edit2.setText(setdata[3]);
                edit3.setText(setdata[4]);
                text2.setText(setdata[5]);
            }
        }


        page1.setVisibility(View.GONE);
        page2.setVisibility(View.VISIBLE);
    }

    public void turn(View v){
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);
    }

    private void LoadToDatabase(String ID, String Password, String Name,String Department, String Rank,String AC, final String address){

        class LoadToData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(user.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
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
    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) { }
    }

    public void update(View v){
        String a1,a2,a3;
        String address=ip+"update2.php";
        a1=edit1.getText().toString();
        a2=edit2.getText().toString();
        a3=edit3.getText().toString();

        LoadToDatabase(ID,"0",a1,a2,a3,"0",address);
        sleep(2000);
        if(result.equals("수정완료"))
            Toast.makeText(this,"수정완료",Toast.LENGTH_SHORT).show();
    }

    public void page2click(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(page2.getWindowToken(), 0);
    }
    public void page3click(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(page3.getWindowToken(), 0);
    }
    public void pwupdate(View v){
        page3.setVisibility(View.VISIBLE);
        page1.setVisibility(View.GONE);
    }
    public void page3back(View v){
        page3.setVisibility(View.GONE);
        page1.setVisibility(View.VISIBLE);
    }

    public void page3update(View v){
        String p1=pw1.getText().toString();
        String p2=pw2.getText().toString();
        String p3=pw3.getText().toString();
        String address=ip+"pwupdate.php";
        if(!p1.equals(Password))
            Toast.makeText(this,"현재비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
        else if(p2.length()>=10)
            Toast.makeText(this,"비밀번호는 최대 10자리입니다.",Toast.LENGTH_SHORT).show();
        else if(p2.isEmpty())
            Toast.makeText(this,"수정할 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
        else if(!p2.equals(p3))
            Toast.makeText(this,"비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
        else{
            LoadToDatabase(ID,p3,"0","0","0","0",address);
            Toast.makeText(this,"수정완료",Toast.LENGTH_SHORT).show();
            pw1.setText("");
            pw2.setText("");
            pw3.setText("");
            page3.setVisibility(View.GONE);
            page1.setVisibility(View.VISIBLE);
        }
    }
}
