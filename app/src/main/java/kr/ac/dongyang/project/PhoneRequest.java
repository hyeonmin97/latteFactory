package kr.ac.dongyang.project;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PhoneRequest extends StringRequest {

    // 서버 url 설정 (php 파일 연동)
    final static private String URL = "http://122.32.165.55/phone.php";
    private Map<String, String> map;

    public PhoneRequest(String id, String emCol2, Response.Listener<String> listener ) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("id", id);
        map.put("emCol2", emCol2);
        //map.put("emCol3", emCol3);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
