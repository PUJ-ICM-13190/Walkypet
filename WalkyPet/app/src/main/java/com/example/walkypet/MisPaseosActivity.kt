package com.example.walkypet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityMisPaseosBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class MisPaseosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisPaseosBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisPaseosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
