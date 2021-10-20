package kr.ac.dongyang.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInfo extends AppCompatActivity {
    TextView edit_id, user_name;
    EditText user_pass, user_phone, user_email, user_emer1, user_emer2, user_emer3, user_disease, user_medicine;
    private Button user_update, user_quit;
    String name, password, phone, email, disease, medicine, emCol1, emCol2, emCol3;
    //int selected;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        edit_id = findViewById(R.id.edit_id);
        user_name = findViewById(R.id.user_name);
        user_pass = findViewById(R.id.user_pass);
        user_phone = findViewById(R.id.user_phone);
        user_email = findViewById(R.id.user_email);
        user_emer1 = findViewById(R.id.user_emer1);
        user_emer2 = findViewById(R.id.user_emer2);
        user_emer3 = findViewById(R.id.user_emer3);
        user_disease = findViewById(R.id.user_disease);
        user_medicine = findViewById(R.id.user_medicine);
        user_update = findViewById(R.id.user_update);
        user_quit = findViewById(R.id.user_quit);
        //selected = R.drawable.unspecified;

        SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        String loginId = setting.getString("id","");




        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");


                    if (success) { // 정보 select 성공
                        String U_name = jsonResponse.optString("name");
                        String U_phone = jsonResponse.optString("phone");
                        String U_email = jsonResponse.optString("email");
                        String U_emCol1 = jsonResponse.optString("emCol1");
                        String U_emCol2 = jsonResponse.optString("emCol2");
                        String U_emCol3 = jsonResponse.optString("emCol3");
                        String U_disease = jsonResponse.optString("disease");
                        String U_medicine = jsonResponse.optString("medicine");
                        Toast.makeText(getApplicationContext(), "불러왔습니다.", Toast.LENGTH_SHORT).show();

                        editor.putString("name", U_name);
                        editor.putString("phone", U_phone);
                        editor.putString("email", U_email);
                        editor.putString("emCol1", U_emCol1);
                        editor.putString("emCol2", U_emCol2);
                        editor.putString("emCol3", U_emCol3);
                        editor.putString("disease", U_disease);
                        editor.putString("medicine", U_medicine);
                        editor.apply();
                        editor.commit();
                        //Intent intent = new Intent(MyInfo.this, MainActivity2.class);
                        //startActivity(intent);
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
        MyInfoSelect myinfoSelect = new MyInfoSelect(loginId, name, phone, email, emCol1, emCol2, emCol3, disease, medicine, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyInfo.this);
        queue.add(myinfoSelect);








        name = setting.getString("name", "");
        password = setting.getString("password", "");
        phone = setting.getString("phone", "");
        email = setting.getString("email", "");
        disease = setting.getString("disease", "");
        medicine = setting.getString("medicine", "");
        emCol1 = setting.getString("emCol1", "");
        emCol2 = setting.getString("emCol2", "");
        emCol3 = setting.getString("emCol3", "");
        Log.d("idddd", loginId);
        Log.d("nammmmmm", name);

        edit_id.setText(loginId);
        user_name.setText(name);
        user_pass.setText(password);
        user_phone.setText(phone);
        user_email.setText(email);
        user_emer1.setText(emCol1);
        user_emer2.setText(emCol2);
        user_emer3.setText(emCol3);
        user_disease.setText(disease);
        user_medicine.setText(medicine);





        // update 버튼 눌렀을 때
        user_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = user_pass.getText().toString();
                phone = user_phone.getText().toString();
                email = user_email.getText().toString();
                emCol1 = user_emer1.getText().toString();
                emCol2 = user_emer2.getText().toString();
                emCol3 = user_emer3.getText().toString();
                disease = user_disease.getText().toString();
                medicine = user_medicine.getText().toString();


                // 입력하지 않았을 경우
                if (password.equals("") || phone.equals("") || email.equals("") || emCol1.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyInfo.this);
                    dialog = builder.setMessage("필수항목을 모두 입력해주세요").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyInfo.this);
                builder.setCancelable(false);
                builder.setMessage("수정하시면 이전의 정보들은 모두 변경됩니다. 정말 수정하시겠습니까?");
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                });
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");


                                    if (success) { // 정보 update 성공
                                        String U_pass = jsonResponse.optString("password");
                                        String U_phone = jsonResponse.optString("phone");
                                        String U_email = jsonResponse.optString("email");
                                        String U_emCol1 = jsonResponse.optString("emCol1");
                                        String U_emCol2 = jsonResponse.optString("emCol2");
                                        String U_emCol3 = jsonResponse.optString("emCol3");
                                        String U_disease = jsonResponse.optString("disease");
                                        String U_medicine = jsonResponse.optString("medicine");
                                        Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();

                                        editor.putString("password", U_pass);
                                        editor.putString("phone", U_phone);
                                        editor.putString("email", U_email);
                                        editor.putString("emCol1", U_emCol1);
                                        editor.putString("emCol2", U_emCol2);
                                        editor.putString("emCol3", U_emCol3);
                                        editor.putString("disease", U_disease);
                                        editor.putString("medicine", U_medicine);
                                        editor.apply();
                                        editor.commit();
                                        Intent intent = new Intent(MyInfo.this, MainActivity2.class);
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
                        MyInfoRequest myinfoRequest = new MyInfoRequest(loginId, password, phone, email, emCol1, emCol2, emCol3, disease, medicine, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(MyInfo.this);
                        queue.add(myinfoRequest);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        // 탈퇴 버튼 눌렀을 때
        user_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyInfo.this);
                builder1.setCancelable(false);
                builder1.setMessage("탈퇴하시면 회원님의 정보는 모두 삭제됩니다. 정말 탈퇴하시겠습니까?");
                builder1.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                });
                builder1.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");

                                    if (success) { // 정보 update 성공
                                        Toast.makeText(getApplicationContext(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                        editor.clear();
                                        editor.commit();
                                        Intent intent = new Intent(MyInfo.this, MainActivity.class);
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
                        MyInfoDelete myinfoDelete = new MyInfoDelete(loginId, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(MyInfo.this);
                        queue.add(myinfoDelete);
                    }
                });
                AlertDialog dialog = builder1.create();
                dialog.show();

            }
        });
    }
}
