package com.example.myview.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myview.databinding.ActivityHbLevelBinding;

public class HBLevelActivity extends AppCompatActivity {

    private ActivityHbLevelBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHbLevelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.levelView.resetLevelProgress(0, 1000, 776);
    }
}
