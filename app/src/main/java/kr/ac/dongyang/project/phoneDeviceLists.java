package kr.ac.dongyang.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class phoneDeviceLists extends AppCompatActivity {

    ListView listView;
    Phone_Adapter adapter=new Phone_Adapter();
    String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_device_lists);

        Button button = findViewById(R.id.button);
        Button button_exit =findViewById(R.id.button_exit);
        listView = (ListView) findViewById(R.id.list_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            String getName= data.getStringExtra("User_name");
            int getId=data.getIntExtra("User_gender",0);
            String getNumber=data.getStringExtra("User_number");

            adapter.addItem(new Person(getName,getId,getNumber));
            listView.setAdapter(adapter);
        }
    }

    public void add(View v){
        Intent intent = new Intent(this,PhoneActivity.class);
        startActivityForResult(intent, 101);
    }
    class Phone_Adapter extends BaseAdapter {
        ArrayList<Person> items;


        public Phone_Adapter(){
            items = new ArrayList<Person>();
        }
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Person item){
            items.add(item);
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if(convertView==null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.phone_info, viewGroup, false);
            }

            Phone_info view = new Phone_info(getApplicationContext());
            Person item= items.get(i);
            view.setName(item.getName());
            view.setImage(item.getGender_ID());
            view.setNumber(item.getNumber());

            phone_number=item.getNumber();

            Button button = view.button;

            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent myIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone_number));
                    startActivity(myIntent);
                    finish();
                }
            });
            return view;
        }
    }

    @Override
    public void onBackPressed() {    }

    //종료 버튼 누를시 실행창 뜨기
    public void onClickExit(View v){
        AlertDialog.Builder builder= new AlertDialog.Builder(phoneDeviceLists.this);
        builder.setMessage("현재 화면에서 나가시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
}