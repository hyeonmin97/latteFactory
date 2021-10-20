package kr.ac.dongyang.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ssstest","ssstets");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        ImageView gif_image = (ImageView) findViewById(R.id.imageView2);
        Glide.with(this).load(R.drawable.animation).into(gif_image);


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver( mMessageReceiver, new IntentFilter("custom-event-name"));

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver( mMessageReceiver);

        super.onPause();
    }
}