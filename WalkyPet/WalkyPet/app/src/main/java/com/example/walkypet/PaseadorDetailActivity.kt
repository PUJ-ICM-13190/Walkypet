package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseadorDetailBinding

class PaseadorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseadorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir datos del paseador
        val paseador = intent.getParcelableExtra<Paseador>("paseador")
        paseador?.let {
            binding.textViewPaseadorName.text = it.nombre
            binding.textViewPaseadorRazas.text = "Razas: ${it.raza}"
            binding.textViewPaseadorHorario.text = "Horario: ${it.horario}"
        }

        // Acción al presionar "Agendar Paseo"
        binding.buttonAgendarPaseo.setOnClickListener {
            val intent = Intent(this, PlanesPaseoActivity::class.java)
            startActivity(intent)
        }
    }
}
