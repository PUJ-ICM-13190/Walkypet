package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseadorMainBinding

class PaseadorMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseadorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes establecer datos ficticios
        binding.textViewEarnings.text = "Ganancias: $150.00"
        binding.textViewScheduledWalks.text = "Paseos Programados: 3"

        // Configurar el botón "Crear Nuevo Paseo"
        binding.buttonNewWalk.setOnClickListener {
            // Iniciar la actividad MapsPaseadorActivity cuando se haga clic en el botón
            val intent = Intent(this, MapsPaseadorActivity::class.java)
            startActivity(intent)
        }

        // Configurar la barra de navegación
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    // Aquí puedes cambiar de actividad o fragment si fuese necesario
                    true
                }
                R.id.nav_perfil -> {
                    // Navegar a PerfilPaseador
                    val intent = Intent(this, PerfilPaseador::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}