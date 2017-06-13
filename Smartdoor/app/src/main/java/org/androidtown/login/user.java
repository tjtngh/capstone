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
import android.widget.EditText;
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
import java.util.Set;
import java.util.UUID;

/**
 * Created by WBHS on 2017-05-08.
 */
public class user extends AppCompatActivity{


    SharedPreferences setting;
    SharedPreferences.Editor editor;

    RelativeLayout page1,page2,page3,page4;

    TextView text1,text2;
    EditText edit1,edit2,edit3,pw1,pw2,pw3;
    String ID,Password,result,AUTO;
    String ip="http://192.168.123.100/";
    TextView beaconlist,asdf;
    String AC;
    String number;
    int next=1;
    int connect=0;

    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevcie;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';
    Thread mWorkerThread = null; // 문자열 수신 쓰레드.
    byte[] readBuffer; //수신 버퍼
    int readBufferPosition; // 버퍼 내 수신 문자 저장 위치



    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        Password=intent.getStringExtra("PW");
        AUTO=intent.getStringExtra("AUTO");

        page1=(RelativeLayout)findViewById(R.id.page1);
        page2=(RelativeLayout)findViewById(R.id.page2);
        page3=(RelativeLayout)findViewById(R.id.page3);
        page4=(RelativeLayout)findViewById(R.id.page4);

        text1=(TextView)findViewById(R.id.teid);
        text2=(TextView)findViewById(R.id.teac);
        edit1=(EditText)findViewById(R.id.edname);
        edit2=(EditText)findViewById(R.id.eddepart);
        edit3=(EditText)findViewById(R.id.edrank);

        pw1=(EditText)findViewById(R.id.pw1ed);
        pw2=(EditText)findViewById(R.id.pw2ed);
        pw3=(EditText)findViewById(R.id.pw3ed);

        beaconlist=(TextView)findViewById(R.id.beaconlist);
        asdf=(TextView)findViewById(R.id.asdf);


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
                        Toast.makeText(user.this, "기능을 취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            }
        }

        String address=ip+"setdata3.php";
        String setdata[];
        LoadToDatabase(ID, "0", "0", "0", "0", "0", address);

        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }

        Log.e("log_tag", "로그요:"+result);
        if (result != null) {
            setdata = result.split("-");
            text1.setText(setdata[0]);
            edit1.setText(setdata[2]);
            edit2.setText(setdata[3]);
            edit3.setText(setdata[4]);
            text2.setText(setdata[5]);
            AC=setdata[5];
        }
        handler.sendEmptyMessage(0);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);

  }

    @Override
    protected void onDestroy() {
        if(AUTO.equals("NO")){
            Intent intent = new Intent(user.this,service1.class);
            stopService(intent);
        }
        else{
        }
        handler.removeMessages(0);
        super.onDestroy();
    }
    public void adclick1(View v) {

        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }
        Intent intent = new Intent(user.this,service1.class);
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

    public void btninfo(View v){
        String address=ip+"setdata3.php";
        String setdata[];

        if(!text1.getText().toString().equals(ID)) {
            LoadToDatabase(ID, "0", "0", "0", "0", "0", address);
            while(true){
                if(next==100) {
                    next = 1;
                    break;
                }
            }
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
        while(true){
            if(next==100) {
                next = 1;
                break;
            }
        }
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
    public void beaconinfo(View v){
        page4.setVisibility(View.VISIBLE);
        page1.setVisibility(View.GONE);

        if(AC!=null)
        if(AC.equals("1")) {
            beaconlist.append("제1구역\n");
            beaconlist.append("제2구역\n");
            beaconlist.append("제3구역\n");
        }
        else if(AC.equals("2")){
            beaconlist.append("제2구역\n");
            beaconlist.append("제3구역\n");
        }
        else if(AC.equals("3")){
            beaconlist.append("제3구역\n");
        }
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
            if(AC.equals("1"))
            {
                Toast.makeText(this,"문열림",Toast.LENGTH_SHORT).show();
                    sendData("first");
            }
            else if(AC.equals("2"))
            {
                    Toast.makeText(this,"문열림",Toast.LENGTH_SHORT).show();
                    sendData("second");
            }
            else if(AC.equals("3"))
            {
                    Toast.makeText(this,"문열림",Toast.LENGTH_SHORT).show();
                    sendData("third");
            }
            else{
            }
    }
    public void closedoor(View v){
        sendData("b");
        Toast.makeText(this,"문닫힘",Toast.LENGTH_SHORT).show();
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(connect==0){
                checkBluetooth();
                connect=1;}
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public void servicestart(View v) {
        Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show();

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.removeMessages(0);
        Intent intent = new Intent(user.this,service1.class);
        intent.putExtra("AC",AC);
        startService(intent);
    }
    public void serviceend(View v){
        Toast.makeText(getApplicationContext(),"Service 끝",Toast.LENGTH_SHORT).show();
        handler.sendEmptyMessage(0);
        Intent intent = new Intent(user.this,service1.class);
        stopService(intent);
    }
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
