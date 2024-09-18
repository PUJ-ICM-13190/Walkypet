package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkypet.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private lateinit var paseadoresList: List<Paseador>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crear algunos paseadores de ejemplo
        paseadoresList = listOf(
            Paseador("Juan", "Pequeñas", "8:00 AM - 12:00 PM"),
            Paseador("Carlos", "Medianas", "10:00 AM - 2:00 PM"),
            Paseador("Ana", "Grandes", "12:00 PM - 4:00 PM"),
            Paseador("Lucía", "Pequeñas y Medianas", "2:00 PM - 6:00 PM"),
            Paseador("Pedro", "Grandes", "4:00 PM - 8:00 PM")
        )

        // Crear el adaptador personalizado
        val adapter = PaseadorAdapter(this, paseadoresList)
        binding.listViewPaseadores.adapter = adapter

        // Configurar el ListView para que responda al clic en un paseador
        binding.listViewPaseadores.setOnItemClickListener { _, _, position, _ ->
            val paseador = paseadoresList[position]
            val intent = Intent(this, PaseadorDetailActivity::class.java) // Cambiar a PaseoActivity
            intent.putExtra("paseador", paseador)
            startActivity(intent)
        }

        // Configurar la barra de navegación
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    // Aquí puedes cambiar de actividad o fragment si fuese necesario
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
