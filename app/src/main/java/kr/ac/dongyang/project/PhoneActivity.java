package kr.ac.dongyang.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.UserDictionary.Words._ID;

public class PhoneActivity extends AppCompatActivity {

    EditText editText_phone_number;
    private Button button_save, button_cancel;
    String phone;
    //int selected;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);


        editText_phone_number = findViewById(R.id.editText_phone_number);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        //selected = R.drawable.unspecified;

        SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        String loginId = setting.getString("id","");
        Log.d("idddd", loginId);


        // save 버튼 눌렀을 때
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = editText_phone_number.getText().toString();


                // 입력하지 않았을 경우
                if (phone.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneActivity.this);
                    dialog = builder.setMessage("번호를 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) { // 비상연락처 추가 성공
                                String E_phone = jsonResponse.optString("phone");
                                Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();


                                editor.putString("emCol2", E_phone);
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(PhoneActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                                editor.clear();
                                editor.commit();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                PhoneRequest phoneRequest = new PhoneRequest(loginId, phone, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PhoneActivity.this);
                queue.add(phoneRequest);


            }


        });

        // cancel 버튼 눌렀을 때
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }


}