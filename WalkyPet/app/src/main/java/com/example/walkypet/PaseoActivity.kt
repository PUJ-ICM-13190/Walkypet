package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseoBinding

class PaseoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar tarjeta del paseo en proceso
        binding.cardPaseoEnProceso.setOnClickListener {
            val intent = Intent(this, PaseoEnProcesoDetailsActivity::class.java)
            startActivity(intent)
        }

        // Configurar tarjeta del paseo finalizado
        binding.cardPaseoFinalizado.setOnClickListener {
            // Acción para calificar el paseo, si es necesario
        }
        // Configurar la barra de navegación
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_paseo -> {
                    // Navegar a PaseoActivity
                    val intent = Intent(this, PaseoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Navegar a ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
