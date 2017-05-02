package org.androidtown.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WBHS on 2017-04-30.
 */
public class administrator extends AppCompatActivity {
    private Button back;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    ListView listView;
    EditText search;
    Button btn1;
    RelativeLayout page2;
    RelativeLayout page1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator);
        back=(Button)findViewById(R.id.back);
        listView=(ListView)findViewById(R.id.listview);
        search=(EditText)findViewById(R.id.editsearch);
        btn1=(Button)findViewById(R.id.search);
        page2=(RelativeLayout)findViewById(R.id.page2);
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_item=(String)adapterView.getItemAtPosition(i);
                search.setText(selected_item);
            }
        });
        list.add("사과");
        list.add("배");
        list.add("귤");
        list.add("수박");
        list.add("참외");
        list.add("파인애플");
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
    }

