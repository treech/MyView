package com.example.myview.ui;


import android.os.Bundle;

import androidx.activity.ComponentActivity;

import com.example.myview.R;
import com.example.myview.ui.view.FlowLayout;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class TagLayoutActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_layout);
        FlowLayout mFlowLayout = findViewById(R.id.flowLayout);
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
//        mFlowLayout.setAlignByCenter(FlowLayout.AlienState.CENTER);
        mFlowLayout.setAdapter(list, R.layout.item, new FlowLayout.ItemView<String>() {
            @Override
            public void getCover(String item, FlowLayout.ViewHolder holder, View inflate, int position) {
                holder.setText(R.id.tv_label_name,item);
            }
        });


    }
}
