package com.wfy.simple.arouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wfy.simple.library.Route;


@Route(path = "/app/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
