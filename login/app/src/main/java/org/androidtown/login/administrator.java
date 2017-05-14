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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WBHS on 2017-04-30.
 */
public class administrator extends AppCompatActivity {
    private Button back;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    ListView listView,listView2;
    EditText search,edac,edac2;
    TextView edid,edname,eddepartment,edrank;
    TextView teid,tename,tedepartment,terank,tepw;
    Button btn1,btn2,deletebtn;
    RelativeLayout page1,page2,page3;

    String result;
    String ip="http://192.168.123.100/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator);

        back=(Button)findViewById(R.id.back);
        btn1=(Button)findViewById(R.id.search);
        btn2=(Button)findViewById(R.id.info);
        deletebtn=(Button)findViewById(R.id.delete);

        listView=(ListView)findViewById(R.id.listview);
        listView2=(ListView)findViewById(R.id.listview2);
        search=(EditText)findViewById(R.id.editsearch);

        page3=(RelativeLayout)findViewById(R.id.page3);
        page2=(RelativeLayout)findViewById(R.id.page2);
        page1=(RelativeLayout)findViewById(R.id.page1);

        edid=(TextView)findViewById(R.id.edid);
        edname=(TextView) findViewById(R.id.edname);
        eddepartment=(TextView) findViewById(R.id.eddepart);
        edrank=(TextView) findViewById(R.id.edrank);
        edac=(EditText)findViewById(R.id.edac);

        teid=(TextView)findViewById(R.id.edid2);
        tepw=(TextView)findViewById(R.id.tepw);
        tename=(TextView)findViewById(R.id.edname2);
        tedepartment=(TextView)findViewById(R.id.eddepart2);
        terank=(TextView)findViewById(R.id.edrank2);
        edac2=(EditText)findViewById(R.id.edac2);

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
    public void page2click(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(page2.getWindowToken(), 0);
    }
    public void page3click(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(page3.getWindowToken(), 0);
    }
    public void btnclick1 (View v) {
        String name = search.getText().toString();
        String address1 = ip+"select.php";
        final String address2 = ip+"setdata.php";
        LoadToDatabase("0", "0", name,"0","0","0",address1);
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, list);
        String itemlist[];
        final String[][] divide = new String[1][6];
        final String[][] setdata = new String[1][6];

        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_item = (String) adapterView.getItemAtPosition(i);
                divide[0] =selected_item.split("-");
                LoadToDatabase("0", "0", divide[0][0], divide[0][1], divide[0][2],"0", address2);
                sleep(1500);
                setdata[0] =result.split("-");
                edid.setText(setdata[0][0]);
                edname.setText(setdata[0][1]);
                eddepartment.setText(setdata[0][2]);
                edrank.setText(setdata[0][3]);
                edac.setText(setdata[0][4]);

            }
        });

        sleep(2000);
            if(result.equals("아이디를 확인해주세요")){
            }
            else{
        itemlist = result.split("<br>");
        for (int i = 0; i < itemlist.length; i++) {
            list.add(itemlist[i]);
        }
        }


        edid.setText("");
        edrank.setText("");
        eddepartment.setText("");
        edname.setText("");
        edac.setText("");
    }


    private void LoadToDatabase(String ID, String Password, String Name,String Department, String Rank,String AC, final String address){

        class LoadToData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(administrator.this, "Please Wait", null, true, true);
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

    public void turnback(View v){
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }
    public void btninfo(View v){
        page2.setVisibility(View.VISIBLE);
        page1.setVisibility(View.GONE);
        search.setText("");
        edid.setText("");
        edrank.setText("");
        eddepartment.setText("");
        edname.setText("");
        edac.setText("");
    }
    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) { }
    }
    public void deleteID(View v){
        String ID=edid.getText().toString();
        String address = ip+"delete.php";
        LoadToDatabase(ID,"0","0","0","0","0",address);
        edid.setText("");
        edrank.setText("");
        eddepartment.setText("");
        edname.setText("");
        edac.setText("");
        listView.setVisibility(View.GONE);
        sleep(2000);
        this.btnclick1(page2);
        Toast.makeText(this,"삭제완료",Toast.LENGTH_SHORT).show();
}

    public void deleteID2(View v){
        String ID=teid.getText().toString();
        String address = ip+"delete2.php";
        LoadToDatabase(ID,"0","0","0","0","0",address);
        teid.setText("");
        tepw.setText("");
        terank.setText("");
        tedepartment.setText("");
        tename.setText("");
        edac2.setText("");
        listView2.setVisibility(View.GONE);
        sleep(2000);
        this.btnsignup(page3);
        Toast.makeText(this,"삭제완료",Toast.LENGTH_SHORT).show();

    }

    public void updatebtn(View v){
        String ID=edid.getText().toString();
        String ac=edac.getText().toString();
        String address = ip+"ACupdate.php";
        LoadToDatabase(ID,"0","0","0","0",ac,address);
        sleep(2000);

        if(result.equals("수정완료")){
            Toast.makeText(this,"수정완료",Toast.LENGTH_SHORT).show();
            listView.setVisibility(View.GONE);
            edid.setText("");
            edrank.setText("");
            eddepartment.setText("");
            edname.setText("");
            edac.setText("");
            this.btnclick1(page2);
        }
        else
            Toast.makeText(this,"수정실패",Toast.LENGTH_SHORT).show();
    }
    public void btnsignup (View v){
        page3.setVisibility(View.VISIBLE);
        page1.setVisibility(View.GONE);

        String address1 = ip+"select2.php";
        final String address2 = ip+"setdata2.php";
        LoadToDatabase("0", "0","0","0","0","0", address1);

        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, list);
        listView2.setVisibility(View.VISIBLE);
        listView2.setAdapter(adapter);


        final String[][] setdata = new String[1][6];
        String [] itemlist;
        final String[] resultid = new String[1];

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_item = (String) adapterView.getItemAtPosition(i);
                resultid[0] =selected_item;
                LoadToDatabase(resultid[0], "0", "0","0","0","0", address2);
                sleep(1500);
                setdata[0] =result.split("-");
                teid.setText(setdata[0][0]);
                tepw.setText(setdata[0][1]);
                tename.setText(setdata[0][2]);
                tedepartment.setText(setdata[0][3]);
                terank.setText(setdata[0][4]);
                edac2.setText("");
            }
        });

        sleep(1500);

            itemlist = result.split("<br>");
            for (int i = 0; i < itemlist.length; i++) {
                list.add(itemlist[i]);
            }

    }
    public void turnback2(View v){
        page3.setVisibility(View.GONE);
        page1.setVisibility(View.VISIBLE);
    }

    public void updatebtn2(View v){
        String ID=teid.getText().toString();
        String PW=tepw.getText().toString();
        String na=tename.getText().toString();
        String de=tedepartment.getText().toString();
        String rk=terank.getText().toString();
        String ac=edac2.getText().toString();
        String address1 = ip+"delete2.php";
        String address2 = ip+"info.php";
        if(!ac.isEmpty()) {
            LoadToDatabase(ID, "0", "0", "0", "0", "0", address1);
            LoadToDatabase(ID, PW, na, de, rk, ac, address2);
            sleep(2000);
            this.btnsignup(page3);
            Toast.makeText(this,"승인완료",Toast.LENGTH_SHORT).show();

            teid.setText("");
            tepw.setText("");
            tename.setText("");
            tedepartment.setText("");
            terank.setText("");
            edac2.setText("");
        }
        else
            Toast.makeText(this,"접근권한을 설정해주세요",Toast.LENGTH_SHORT).show();

    }

}

