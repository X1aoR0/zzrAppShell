package com.zzrr.testshell;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zzrr.testshell.databinding.ActivityMainBinding;

import org.w3c.dom.Text;

import java.io.File;

import dalvik.system.PathClassLoader;

public class MainActivity extends Activity {

    // Used to load the 'testshell' library on application startup.
    static {
        System.loadLibrary("testshell");
    }
    private Button btn_startSecond;
    private String TAG = "testApp";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        Log.d(TAG,"R's classloader" + R.class.getClassLoader().toString());
        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        btn_startSecond = (Button) findViewById(R.id.button);
        btn_startSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                Log.d(TAG,getClassLoader().toString());
            }
        });
    }

    /**
     * A native method that is implemented by the 'testshell' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}