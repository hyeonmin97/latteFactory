package kr.ac.dongyang.project.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import kr.ac.dongyang.project.GpsTracker;
import kr.ac.dongyang.project.LoadingDialog;
import kr.ac.dongyang.project.MainActivity;
import kr.ac.dongyang.project.MainActivity2;
import kr.ac.dongyang.project.R;
import kr.ac.dongyang.project.RetrofitInit;
import kr.ac.dongyang.project.SplashActivity;
import kr.ac.dongyang.project.bluetooth.BluetoothActivity;
import kr.ac.dongyang.project.bluetooth.BluetoothController;
import kr.ac.dongyang.project.dto.CustomerDTO;
import kr.ac.dongyang.project.dto.GyroDTO;
import kr.ac.dongyang.project.message;
import retrofit2.Call;
import retrofit2.Callback;

public class bluetoothService extends Service {
    private GpsTracker gpsTracker;
    String id;
    MysqlInterface api;
    private static final String TAG = "bluetoothService";
    BluetoothController btcl;
    BluetoothAdapter mBTAdapter;
    private BluetoothSocket raspberrySocket = null;
    Handler handler;
    ConnectedThread thread;

    private static final String BLUETOOTH_END = "bted";
    @Override
    public void onCreate() {
        //저장된 아이디 가져오기
        SharedPreferences setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", "");
        Log.d(TAG, "oncreate id =" + id);

        api = RetrofitInit.getRetrofit().create(MysqlInterface.class);

        btcl = BluetoothController.getController();
        mBTAdapter = btcl.getmBTAdapter();
        handler= new Handler();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //호출될때마다 실행
        Log.d(TAG, "onStartCommand");
        String button = null;

        try {
            //블투액티비티에서 호출한 경우
            Boolean createSocket = intent.getBooleanExtra("bluetooth",false);
            if(createSocket){//bluetooth느ㄴ 연결 성공시 넘어옴(bluetoothService, mainactivity2)

            //블루투스 소켓 연결
                thread = new ConnectedThread(btcl.getRaspberrySocket());
                thread.start();
                thread.write("connect".getBytes());
                Log.d(TAG, "sockConnect while");

            }
        } catch (Exception e){

        }
        try {
            button = intent.getStringExtra("button"); //message에서 버튼을 눌렀을 때
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //버튼값이 있는 경우
        if (button != null) {

            Log.d(TAG, "input button");
            if (button.equals("btnY")) {
                Log.d(TAG, "btnY");

                //문자 보내기
                gpsTracker = new GpsTracker(getApplicationContext());
                double latitude = gpsTracker.getLatitude();//위도
                double longitude = gpsTracker.getLongitude();//경도
                String address = getCurrentAddress(latitude, longitude);//한글주소


                //전화번호 가져오기
                Call<CustomerDTO> customerInfo = api.getPhoneNumber(id);
                customerInfo.enqueue(new Callback<CustomerDTO>() {
                    @Override
                    public void onResponse(Call<CustomerDTO> call, retrofit2.Response<CustomerDTO> response) {
                        CustomerDTO customerDTO = response.body();
                        //문자보내기
                        if (customerDTO.getId() != null) {
                            String emCol = customerDTO.getEmCol1();
                            String sendMessage = "https://www.google.com/maps/place/" + latitude + "," + longitude;
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(emCol, null, sendMessage, null, null);//문자전송
                            if (customerDTO.getEmCol2().length() > 5) {
                                String emCol2 = customerDTO.getEmCol2();
                                sms.sendTextMessage(emCol2, null, sendMessage, null, null);//문자전송
                            }
                            if (customerDTO.getEmCol3().length() > 5) {
                                String emCol3 = customerDTO.getEmCol3();
                                sms.sendTextMessage(emCol3, null, sendMessage, null, null);//문자전송
                            }
                        } else {
                            Log.d(TAG, "getPhoneNumber fail");
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerDTO> call, Throwable t) {
                        Log.d(TAG, "customerInfo fail");
                    }
                });


            }//bunY

            else if (button.equals("btnN")) {
                Log.d(TAG, "btnN");
            }

            //넘어진경우, 넘어지지 않은경우 모두 포함
            Call<GyroDTO> gyro = api.updateGyro(id, 0);
            gyro.enqueue(new Callback<GyroDTO>() {
                @Override
                public void onResponse(Call<GyroDTO> call, retrofit2.Response<GyroDTO> response) {
                    if (response.body() == null) {
                        Log.d(TAG, "response null");
                    }
                    if (response.body().isSuccess() == true) {
                        Log.d(TAG, "response true");

                    }
                    if (response.body().isSuccess() == false) {
                        Log.d(TAG, "response false");

                    }
                }

                @Override
                public void onFailure(Call<GyroDTO> call, Throwable t) {
                    Log.d(TAG, "fail");
                }
            });
            try {
                thread.write(button.getBytes());
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        try {

            thread.interrupt();//스레드 종료를 위해 인터럽트 발생
            btcl.closeRaspberrySocket();
            Log.d(TAG, "destroy socket.close()");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "tcp onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //GPS 도로명 주소로 변환
    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    public class ConnectedThread extends Thread {
        private static final String TAG = "BluetoothHandler";

        private InputStream mInputStream;
        private OutputStream mOutputStream;
        private BluetoothSocket mBluetoothSocket;

        /**
         * 소켓을 입력받아 연결 스트림을 생성
         *
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();

            }


        }

        @Override
        public void run() {
            byte[] data;
            while (true) {
                try {
                    if (btcl.getRaspberrySocket() == null) {
                        break;
                    }
                    int available = mInputStream.available();
                    if (available > 0) {//값이 있을때
                        data = new byte[available];
                        mInputStream.read(data);
                        String inputData = new String(data, "UTF-8").trim();
                        if(inputData.equals("fallen")){
                            Intent intent = new Intent(getApplicationContext(), message.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

        public void write(byte[] data) {
            try {
                mOutputStream.write(data);
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}