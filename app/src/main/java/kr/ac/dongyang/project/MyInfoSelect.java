package kr.ac.dongyang.project;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyInfoSelect extends StringRequest {
    // 서버 url 설정 (php 파일 연동)
    final static private String URL = "http://122.32.165.55/MyView.php";
    private Map<String, String> map;

    public MyInfoSelect(String id, String name, String phone, String email, String emCol1, String emCol2, String emCol3, String disease, String medicine, Response.Listener<String> listener ) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("id", id);
        map.get(name);
        map.get(phone);
        map.get(email);
        map.get(emCol1);
        map.get(emCol2);
        map.get(emCol3);
        map.get(disease);
        map.get(medicine);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
