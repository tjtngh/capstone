package org.androidtown.login;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by WBHS on 2017-06-05.
 */
public class service2 extends Service implements BeaconConsumer{
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevcie;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    int connect=0;


    String AC,number;

    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();

    @Override
    public void onDestroy() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        beaconManager.unbind(this);
        handler1.removeMessages(0);
        handler2.removeMessages(0);
        super.onDestroy();
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AC=intent.getStringExtra("AC");
        handler2.sendEmptyMessage(0);
        handler1.sendEmptyMessage(0);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            if(connect==0){
                checkBluetooth();
                connect=1;}
            handler2.sendEmptyMessageDelayed(0, 1000);
        }
    };

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        selectDevice();
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
            // 데이터 수신 준비.
        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(),
                    "이미연결중", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("24DDF411-8CF1-440C-87CD-E368DAF9C93E", null, null, null));
        } catch (RemoteException e) {
        }
    }
    Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {

            for (Beacon beacon : beaconList) {
                number= String.valueOf(beacon.getId3());
                if(AC.equals("1"))
                {
                    if((number.equals("12698")||number.equals("12699")||number.equals("12697"))&&beacon.getDistance()<2.5)
                    {
                        sendData("first");
                    }
                }
                else if(AC.equals("2"))
                {
                    if((number.equals("12698")||number.equals("12699"))&&beacon.getDistance()<2.5)
                    {
                        sendData("second");
                    }
                }
                else if(AC.equals("3"))
                {
                    if(number.equals("12699")&&beacon.getDistance()<2.5)
                    {
                        sendData("third");
                    }
                }
                else{
                }
            }
            handler1.sendEmptyMessageDelayed(0, 1000);
        }
    };

}
