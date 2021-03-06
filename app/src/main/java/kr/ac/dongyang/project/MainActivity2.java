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



        //???????????? ????????? ??????
        startService(new Intent(MainActivity2.this, bluetoothService.class));


        //subActivity??? ???????????? ??????
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
        home_user_name.setText(setting.getString("id", "") + "???");


        blbx.setOnClickListener((v) -> {
            //????????? ?????? -> ?????? ????????????, ????????? ????????????

            Intent intent1 = new Intent(this, BlackBoxActivity.class);
            startActivity(intent1);

        });
        back.setOnClickListener((v) -> {
            //????????? ?????? -> ?????? ????????????, ????????? ????????????
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
        //???????????? mac??????
        SharedPreferences device = getSharedPreferences("bluetooth", 0);
        String address = device.getString("raspberry", "");
        if(address.equals("")){
            Toast.makeText(getApplicationContext(),"???????????? ????????????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        }
        else {
            //???????????? ?????? ??????
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
                    //Toast.makeText(getApplicationContext(),"???????????? ????????? ???????????????", Toast.LENGTH_LONG).show();
                }
                else{
                    android.bluetooth.BluetoothDevice btDevice = mBTAdapter.getRemoteDevice(address);
                    try {
                        raspberrySocket = btcl.createRaspberrySocket(btDevice);
                    } catch (IOException e) {//exception ?????? ???
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    try {
                        raspberrySocket.connect();
                        //????????? ???????????? ??? ?????? -> ????????? ??????
                        Intent intent = new Intent(getApplicationContext(), bluetoothService.class);
                        intent.putExtra("bluetooth", true);
                        startService(intent);
                    } catch (IOException e) {//connect() ????????? exception ??????
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "?????? ?????? - ???????????? ????????? ??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                            }
                            
                        });
                        e.printStackTrace();
                        try {
                            raspberrySocket.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    finally {//?????? ?????? ????????? ?????? ?????? ?????? ??????
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

    //????????????
    public void onClick(View view) {
        Intent intentLogout = new Intent(this, MainActivity.class);
        intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentLogout);
        finish();
    }

    // ??????????????? ?????? ?????? ????????? ????????? ?????? ??????
    private long backKeyPressedTime = 0;
    // ??? ?????? ?????? ?????? ????????? ?????? ??? ??????
    private Toast toast;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // ?????? ?????? ?????? ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????

        // ??????????????? ?????? ?????? ????????? ????????? ????????? 1.5?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 1.5?????? ???????????? Toast ??????
        // 2000 milliseconds = 2.0 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "?????? ?????? ????????? ??? ??? ??? ???????????? ???????????????.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.0?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.0?????? ????????? ???????????? ??????
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
            case android.R.id.home:{ // ?????? ?????? ?????? ????????? ???
                mDrawerLayout.openDrawer(GravityCompat.START);
                Intent intent=getIntent();
                String name = intent.getStringExtra("name");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}