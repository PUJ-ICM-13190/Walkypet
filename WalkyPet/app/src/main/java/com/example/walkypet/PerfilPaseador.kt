package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkypet.databinding.ActivityPerfilPaseadorBinding

class PerfilPaseador : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPaseadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPerfilPaseadorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configurar la barra de navegación
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    // Aquí puedes cambiar de actividad o fragment si fuese necesario
                    val intent = Intent(this, PaseadorMainActivity::class.java)
                    startActivity(intent)
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
