package com.example.walkypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityScheduledWalksBinding

class ScheduledWalksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduledWalksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduledWalksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes agregar la lógica para mostrar la lista de paseos programados
    }
}
