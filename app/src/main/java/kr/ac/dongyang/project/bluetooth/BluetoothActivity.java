package kr.ac.dongyang.project.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

import kr.ac.dongyang.project.LoadingDialog;
import kr.ac.dongyang.project.MainActivity2;
import kr.ac.dongyang.project.R;
import kr.ac.dongyang.project.SplashActivity;
import kr.ac.dongyang.project.blackbox.BlackBoxActivity;
import kr.ac.dongyang.project.service.bluetoothService;
import kr.ac.dongyang.project.service.gyroService;

public class BluetoothActivity extends AppCompatActivity {
    private Handler handler;
    SharedPreferences bluetoothDevice;
    SharedPreferences.Editor editor;
    int flag;

    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<android.bluetooth.BluetoothDevice> mPairedDevices;
    private ListView mDevicesListView;

    private String device;

    BluetoothController btcl;
    private BluetoothSocket latteSocket = null;
    private BluetoothSocket raspberrySocket = null;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        handler = new Handler();

        bluetoothDevice = getSharedPreferences("bluetooth",MODE_PRIVATE);
        editor = bluetoothDevice.edit();

        Button latte = (Button)findViewById(R.id.latte);
        Button raspberry = (Button)findViewById(R.id.raspberry);
        textView = (TextView)findViewById(R.id.mac_address);

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        mDevicesListView = (ListView)findViewById(R.id.device_lsit);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        btcl = BluetoothController.getController();
        mBTAdapter = btcl.getmBTAdapter();

        latte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                device="latte";
                String mac = bluetoothDevice.getString("latte","아직 페어링된 장치가 없습니다.");
                textView.setText(device + " : "+ mac);
                listPairedDevices();
            }
        });

        raspberry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                device = "raspberry";
                String mac = bluetoothDevice.getString("raspberry","아직 페어링된 장치가 없습니다.");
                textView.setText(device + " : "+ mac);
                listPairedDevices();
            }
        });

    }

    private void listPairedDevices(){
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (android.bluetooth.BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //로딩이미지 gif 형식


            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            //맥주소 저장
            editor.putString(device, address);
            editor.putString(device+"Name", name);
            editor.apply();
            textView.setText(device + " : "+ address);

            //연결됨
            if(device.equals("raspberry")){
                connectSocket(address);
            }else if(device.equals("latte")){
                finish();
            }
            // Spawn a new thread to avoid blocking the GUI one
//            new Thread()
//            {
//                @Override
//                public void run() {
//                    boolean fail = false;
//                    android.bluetooth.BluetoothDevice btDevice = mBTAdapter.getRemoteDevice(address);
//                    if(device.equals("latte")){
//                        try {
//                            latteSocket = btcl.createLatteSocket(btDevice);
//                        } catch (IOException e) {//exception 발생 시
//                            fail = true;
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        }
//                        // Establish the Bluetooth socket connection.
//                        try {
//                            latteSocket.connect();
//                        } catch (IOException e) {//exception 발생 시
//                            try {
//                                fail = true;
//                                latteSocket.close();
//                            } catch (IOException e2) {
//                                //insert code to deal with this
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getBaseContext(), "라떼판다 저장됨", Toast.LENGTH_SHORT).show();
//                                textView.setText(device + " : "+ address);
//                            }
//                        });
//                    }
//                    else if(device.equals("raspberry")){
//                        try {
//                            raspberrySocket = btcl.createRaspberrySocket(btDevice);
//                        } catch (IOException e) {//exception 발생 시
//                            fail = true;
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                        // Establish the Bluetooth socket connection.
//                        try {
//                            raspberrySocket.connect();
//                        } catch (IOException e) {//exception 발생 시
//                            try {
//                                fail = true;
//                                raspberrySocket.close();
//                            } catch (IOException e2) {
//                                //insert code to deal with this
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getBaseContext(), "라즈베리파이 저장됨", Toast.LENGTH_SHORT).show();
//                                textView.setText(device + " : "+ address);
//                            }
//                        });
//                    }
                    //if(flag == 1) {
//                        try {
//                            mBTSocket = btcl.createBluetoothSocket(device);
//                        } catch (IOException e) {//exception 발생 시
//                            fail = true;
//                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                        }
//                        // Establish the Bluetooth socket connection.
//                        try {
//                            mBTSocket.connect();
//                        } catch (IOException e) {//exception 발생 시
//                            try {
//                                fail = true;
//                                mBTSocket.close();
//                            } catch (IOException e2) {
//                                //insert code to deal with this
//                                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        //정보 있을때
//                        if (!fail) {
//                            startActivity(new Intent(MainActivity.this, VideoStreaming.class));
//                        }
                    //}
//                    else if(flag ==0){
//                        try {
////                            //선택한 디바이스 페어링 요청
////                            Method method = device.getClass().getMethod("createBond", (Class[]) null);
////                            method.invoke(device, (Object[]) null);
//////                            selectDevice = position;
//                            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
//                            Method createBondMethod = class1.getMethod("createBond");
//                            createBondMethod.invoke(device);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            //}.start();

        //}
    };
    private void connectSocket(String address) {
        LoadingDialog loadingDialog = new LoadingDialog(BluetoothActivity.this);
        new Thread() {
            @Override
            public void run() {
                if (address.equals("")){
                    //Toast.makeText(getApplicationContext(),"블루투스 장치를 확인하세요", Toast.LENGTH_LONG).show();
                }
                else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.setCancelable(false);
                            loadingDialog.show();
                        }
                    });
                    // Spawn a new thread to avoid blocking the GUI one

                    android.bluetooth.BluetoothDevice btDevice = mBTAdapter.getRemoteDevice(address);
                    try {
                        raspberrySocket = btcl.createRaspberrySocket(btDevice);
                    } catch (IOException e) {//exception 발생 시
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        raspberrySocket.connect();
                        
                        //성공시 서비스에 값 전달
                        Intent intent = new Intent(getApplicationContext(), bluetoothService.class);
                        intent.putExtra("bluetooth", true);
                        startService(intent);

                        //액티비티 종료
                        finish();

                    } catch (IOException e) {//exception 발생 시
                        try {
                            raspberrySocket.close();
                            btcl.closeRaspberrySocket();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "라즈베리파이의 블루투스를 확인하세요", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e2) {
                            //insert code to deal with this
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    finally {
                        loadingDialog.dismiss();

                    }
                }
            }
        }.start();
    }
}