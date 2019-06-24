package com.wfy.simple.component;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wfy.simple.library.Route;


@Route(path = "/component/main")
public class ComponentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_main);
    }
}
