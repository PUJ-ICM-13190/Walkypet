package com.example.walkypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityAdditionalConfigurationBinding

class AdditionalConfigurationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdditionalConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdditionalConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes configurar la UI para opciones de configuración adicional
    }
}
