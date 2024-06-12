package com.example.myview.ui;


import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import com.example.myview.R;
import com.example.myview.ui.view.MyFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class TagLayoutActivity2 extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_layout2);
        MyFlowLayout flowLayout = findViewById(R.id.flowLayout);
        List<String> list = new ArrayList<>();
        list.add("java");
        list.add("javaEE");
        list.add("javaME");
        list.add("c");
        list.add("php");
        list.add("ios");
        list.add("c++");
        list.add("c#");
        list.add("Android");
        list.add("人工智能");
        list.add("adfadfadfagergergrgerwtcqrqtcqrwtcerwetrcwrt");
        list.add("adfa");
        list.add("adf77");
        flowLayout.setAdapter(list, R.layout.item, new MyFlowLayout.SnapShot<String>() {
            @Override
            public void getHolderView(String item, @NonNull MyFlowLayout.ViewHolder holder, int position) {
                holder.setText(R.id.tv_label_name,item);
            }
        });
    }
}
