package com.example.walkypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityActualizarPerfilBinding

class ActualizarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActualizarPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes configurar la UI para actualizar el perfil
    }
}
