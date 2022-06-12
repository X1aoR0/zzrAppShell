package com.zzrr.testshell;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

public class SecondActivity extends Activity {

    private TextView textView;
    private String TAG = "testSHell";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Resources resources = getResources();
        String str = resources.getString(R.string.secondText);
        Log.d(TAG, "onCreate: " + str);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(R.string.secondText);
    }
}