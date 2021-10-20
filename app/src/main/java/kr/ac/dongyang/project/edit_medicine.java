package kr.ac.dongyang.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class edit_medicine extends Activity {

    private EditText edit_1, edit_2;
    String disease, medicine;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        // 값 가져오기
        edit_1 = findViewById(R.id.edit_1);
        edit_2 = findViewById(R.id.edit_2);
        Button insert = findViewById(R.id.insert);

        SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        String loginId = setting.getString("id","");


        // 입력 버튼이 눌렸을 때
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 입력된 정보를 string으로 가져오기
                disease = edit_1.getText().toString();
                medicine = edit_2.getText().toString();

                //한 칸이라도 입력 안했을 경우
                if (disease.equals("") || medicine.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(edit_medicine.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 입력 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSON 형태로 변형해 받음
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // 결과
                            if(success) { // 성공
                                String E_disease = jsonResponse.optString("disease");
                                String E_medicine = jsonResponse.optString("medicine");
                                Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                                editor.putString("disease",E_disease);
                                editor.putString("medicine",E_medicine);
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(edit_medicine.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                            else { // 성공하지 못했다면
                                if (!(disease == null) || !(medicine == null)) { // null이 아니라면
                                    Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(edit_medicine.this, MainActivity2.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
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
                // Volley 라이브러리를 이용해 실제 서버와 통신을 구현하는 부분
                medicineRequest medicineRequest = new medicineRequest(loginId, disease, medicine, responseListener);
                RequestQueue queue = Volley.newRequestQueue(edit_medicine.this);
                queue.add(medicineRequest);
            }
        });


    }
}