package com.wfy.simple.arouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.wfy.simple.library.Route;
import com.wfy.simple.router.api.Router;


@Route(path = "/app2/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("tag", "click");
                Router.getInstance().go("/component/main");
            }
        });
    }
}
