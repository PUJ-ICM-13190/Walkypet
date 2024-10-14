package com.example.walkypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityMainFragmentBinding

class MainFragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes configurar la UI para la actividad principal
    }
}
