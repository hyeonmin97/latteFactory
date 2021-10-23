package kr.ac.dongyang.project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Context;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import kr.ac.dongyang.project.blackbox.BlackBoxActivity;
import kr.ac.dongyang.project.bluetooth.BluetoothActivity;
import kr.ac.dongyang.project.bluetooth.BluetoothController;
import kr.ac.dongyang.project.service.bluetoothService;
import kr.ac.dongyang.project.service.gyroService;
import kr.ac.dongyang.project.streaming.VideoStreaming;

public class MainActivity2 extends AppCompatActivity {
    ImageButton blbx;
    ImageButton back;
    Button information;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    private static final String TAG = "MA2";
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    Handler handler = new Handler(Looper.getMainLooper());


    BluetoothController btcl;
    BluetoothAdapter mBTAdapter;
    private BluetoothSocket raspberrySocket = null;
    bluetoothService.ConnectedThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_aftermain);



        //낙상감지 서비스 시작
        startService(new Intent(MainActivity2.this, bluetoothService.class));


        //subActivity로 이동하는 버튼
        blbx = findViewById(R.id.blbx);
        back = findViewById(R.id.back);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView home_user_name = navigationView.getHeaderView(0).findViewById(R.id.home_user_name);
        home_user_name.setText(setting.getString("id", "") + "님");


        blbx.setOnClickListener((v) -> {
            //인텐트 선언 -> 현재 액티비티, 넘어갈 액티비티

            Intent intent1 = new Intent(this, BlackBoxActivity.class);
            startActivity(intent1);

        });
        back.setOnClickListener((v) -> {
            //인텐트 선언 -> 현재 액티비티, 넘어갈 액티비티
            Intent intent2 = new Intent(this, VideoStreaming.class);
            startActivity(intent2);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.medicine:
                        Intent NewActivity = new Intent(getApplicationContext(), edit_medicine.class);
                        startActivity(NewActivity);
                        break;

                    case R.id.call:
                        Intent MedActivity = new Intent(getApplicationContext(), phoneDeviceLists.class);
                        startActivity(MedActivity);
                        break;
                    case R.id.timeSet:
                        Intent SetActivity = new Intent(getApplicationContext(), timer.class);
                        startActivity(SetActivity);
                        break;
                    case R.id.update:
                        Intent UpdateActivity = new Intent(getApplicationContext(), MyInfo.class);
                        startActivity(UpdateActivity);
                        break;
                    case R.id.bluetooth:
                        Intent BluetoothDevice = new Intent(getApplicationContext(), BluetoothActivity.class);
                        startActivity(BluetoothDevice);
                        break;

                }
                return true;
            }
        });


        btcl = BluetoothController.getController();
        mBTAdapter = btcl.getmBTAdapter();
        //블루투스 mac주소
        SharedPreferences device = getSharedPreferences("bluetooth", 0);
        String address = device.getString("raspberry", "");
        if(address.equals("")){
            Toast.makeText(getApplicationContext(),"설정에서 라즈베리파이를 지정해주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        }
        else {
            //블루투스 소켓 연결
            connectSocket(address);
        }

        super.onCreate(savedInstanceState);
    }
    private void connectSocket(String address){
        LoadingDialog l = new LoadingDialog(MainActivity2.this);
        new Thread(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.show();

                    }
                });

                if (address.equals("")){
                    //Toast.makeText(getApplicationContext(),"블루투스 장치를 확인하세요", Toast.LENGTH_LONG).show();
                }
                else{
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
                    try {
                        raspberrySocket.connect();
                        //성공시 서비스에 값 전달 -> 스레드 생성
                        Intent intent = new Intent(getApplicationContext(), bluetoothService.class);
                        intent.putExtra("bluetooth", true);
                        startService(intent);
                    } catch (IOException e) {//connect() 실패시 exception 발생
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "연결 에러 - 라즈베리 파이의 블루투스를 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                            
                        });
                        e.printStackTrace();
                        try {
                            raspberrySocket.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    finally {//소켓 연결 여부와 상관 없이 항상 실행
                        l.dismiss();
                    }
                }
                super.run();
            }
        }.start();

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    //로그아웃
    public void onClick(View view) {
        Intent intentLogout = new Intent(this, MainActivity.class);
        intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentLogout);
        finish();
    }

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1.5초가 지났으면 Toast 출력
        // 2000 milliseconds = 2.0 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.0초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.0초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            finishAffinity();
            toast.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        Intent i = new Intent(this, bluetoothService.class);
        stopService(i);
        Log.d(TAG, "main2 onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                Intent intent=getIntent();
                String name = intent.getStringExtra("name");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}