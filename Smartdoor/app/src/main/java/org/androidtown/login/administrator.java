package org.androidtown.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by WBHS on 2017-04-30.
 */
public class administrator extends AppCompatActivity{

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    ListView listView,listView2;
    EditText search,edac,edac2;
    TextView edid,edname,eddepartment,edrank;
    TextView teid,tename,tedepartment,terank,tepw;
    TextView beaconlist;
    Button btn1,btn2,deletebtn;
    RelativeLayout page1,page2,page3,page4;

    String result,AUTO;
    String ip="http://192.168.123.100/";



    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevcie;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    Thread mWorkerThread = null; // 문자열 수신 쓰레드.

    int next=1;
    int connect=0;
    Button serviceend;
    @Override
    protected void onDestroy() {
        if(AUTO.equals("NO")){
            Intent intent = new Intent(administrator.this,service1.class);
            stopService(intent);
        }
        else{
        }
        handler.removeMessages(0);
        super.onDestroy();
    }



    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator);
        Intent intent=getIntent();
        AUTO=intent.getStringExtra("AUTO");

        serviceend=(Button)findViewById(R.id.serviceend);

        btn1=(Button)findViewById(R.id.search);
        btn2=(Button)findViewById(R.id.info);
        deletebtn=(Button)findViewById(R.id.delete);

        listView=(ListView)findViewById(R.id.listview);
        listView2=(ListView)findViewById(R.id.listview2);
        search=(EditText)findViewById(R.id.editsearch);

        page4=(RelativeLayout)findViewById(R.id.page4);
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

        beaconlist=(TextView)findViewById(R.id.beaconlist);

        checkBluetooth();
        int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionResult == PackageManager.PERMISSION_DENIED) {


            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){


                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("권한이 필요합니다.")
                        .setMessage("단말기의 \"위치\" 권한이 필요합니다. 계속하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

                                }

                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(administrator.this, "기능을 취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            }
        }


        handler.sendEmptyMessage(0);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
    }

    public void adclick1(View v) {
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }
        Intent intent = new Intent(administrator.this,service1.class);
        stopService(intent);
        handler.removeMessages(0);

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent2=new Intent(this,MainActivity.class);
        startActivity(intent2);
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
        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }
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
                while(true){
                    if(next==100) {
                        next = 1;
                        break;
                    }
                }
                if(!result.isEmpty()) {
                    setdata[0] = result.split("-");
                    edid.setText(setdata[0][0]);
                    edname.setText(setdata[0][1]);
                    eddepartment.setText(setdata[0][2]);
                    edrank.setText(setdata[0][3]);
                    edac.setText(setdata[0][4]);
                }

            }
        });

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
                    next=100;
                    Log.e("log_tag", "result:"+result);
                    return sb.toString();

                }
                catch(Exception e){
                    next=100;
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
        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }

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
        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }

        this.btnsignup(page3);
        Toast.makeText(this,"삭제완료",Toast.LENGTH_SHORT).show();

    }

    public void updatebtn(View v){
        String ID=edid.getText().toString();
        String ac=edac.getText().toString();
        String address = ip+"ACupdate.php";
        LoadToDatabase(ID,"0","0","0","0",ac,address);
        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }

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

        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }

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
                while(true){
                    if(next==100) {
                        next = 1;
                        break;
                    }
                }
                if(!result.isEmpty()) {
                    setdata[0] = result.split("-");
                    teid.setText(setdata[0][0]);
                    tepw.setText(setdata[0][1]);
                    tename.setText(setdata[0][2]);
                    tedepartment.setText(setdata[0][3]);
                    terank.setText(setdata[0][4]);
                    edac2.setText("");
                }
            }
        });
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
            while(true){
                if(next==100) {
                    next = 1;
                    break;
                }
            }
            LoadToDatabase(ID, PW, na, de, rk, ac, address2);
            while(true){
                if(next==100) {
                    next = 1;
                    break;
                }
            }
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
    public void beaconinfo(View v){
        page4.setVisibility(View.VISIBLE);
        page1.setVisibility(View.GONE);
        beaconlist.setText("");
        beaconlist.append("ID : 12697 / 제1구역\n");
        beaconlist.append("ID : 12698 / 제2구역\n");
        beaconlist.append("ID : 12699 / 제3구역\n");
    }
    public void page4back(View v){
        page4.setVisibility(View.GONE);
        page1.setVisibility(View.VISIBLE);
        beaconlist.setText("");
    }

    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            // finish();  // 앱종료
        } else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }

    }
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            // finish(); // App 종료.
        }
        connectToSelectedDevice("smartbikeway2");
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevcie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와
            //                                           통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는
            //  BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevcie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            connect=1;

        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(),
                    "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            // finish();  // App 종료
        }
    }


    BluetoothDevice getDeviceFromBondedList(String name) {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for (BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    void sendData(String msg){
        try{
            mOutputStream.write(msg.getBytes());
        }catch (Exception e){
        }
    }


    public void opendoor(View v){
        sendData("first");
        Toast.makeText(this,"문열림",Toast.LENGTH_SHORT).show();
    }
    public void closedoor(View v){
        sendData("b");
        Toast.makeText(this,"문닫힘",Toast.LENGTH_SHORT).show();
    }

    public void servicestart(View v) {
        Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show();

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.removeMessages(0);
        Intent intent = new Intent(administrator.this,service1.class);
        startService(intent);
    }
    public void serviceend(View v){
        Toast.makeText(getApplicationContext(),"Service 끝",Toast.LENGTH_SHORT).show();
        handler.sendEmptyMessage(0);
        Intent intent = new Intent(administrator.this,service1.class);
        stopService(intent);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(connect==0){
                checkBluetooth();
                connect=1;}
            handler.sendEmptyMessageDelayed(0,1000);
        }
    };
    BroadcastReceiver bluetoothReceiver =  new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //연결된 장치를 intent를 통하여 가져온다.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            //장치가 연결이 되었으면
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d("TEST", device.getName().toString() +" Device Is Connected!");
                connect=1;
                //장치의 연결이 끊기면
            }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                Log.d("TEST", device.getName().toString() +" Device Is DISConnected!");
                connect=0;
            }
        }
    };
}

