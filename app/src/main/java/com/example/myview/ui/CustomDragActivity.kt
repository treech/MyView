package com.example.myview.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.myview.databinding.ActivityDragViewBinding

class CustomDragActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDragViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = mutableListOf<String>()
        for (index in 0..100){
            list.add("test:$index")
        }
        binding.backView.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
    }

    companion object {
        private const val TAG = "CustomDragActivity"
    }
}