package kr.ac.dongyang.project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Phone_info extends LinearLayout {
    ImageView imageView;
    TextView textView;
    TextView textView2;
    Button button;

    public Phone_info(Context context) {

        super(context);
        init(context);

    }

    public Phone_info(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.phone_info,this,true);

        textView= (TextView) findViewById(R.id.textView_name);
        imageView = (ImageView) findViewById(R.id.imageView2);
        textView2 = (TextView) findViewById(R.id.textView_phone_number);
        button= findViewById(R.id.button_call);

    }


    public void setImage(int imageView_id) {
        this.imageView.setImageResource(imageView_id);
    }


    public void setName(String textView) {
        this.textView.setText(textView);
    }

    public void setNumber(String textView2) {
        this.textView2.setText(textView2);
    }

}
