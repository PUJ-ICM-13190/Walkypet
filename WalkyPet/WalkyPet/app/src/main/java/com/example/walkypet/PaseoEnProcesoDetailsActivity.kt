package com.example.walkypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseoEnProcesoDetailsBinding

class PaseoEnProcesoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseoEnProcesoDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseoEnProcesoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Información inicial del paseo
        binding.status.text = "En ejecución"
        binding.fecha.text = "Fecha: 18/09/2024"
        binding.horaSalida.text = "Hora de salida: 10:30 AM"
        binding.horaLlegada.text = "Hora de llegada: 11:00 AM"
        binding.ubicacionActual.text = "Ubicación: Calle 12, cerca de tu dirección"

        // Configurar los botones
        binding.botonLlamar.setOnClickListener {
            // Código para llamar al paseador
        }

        binding.botonSeguirPaseo.setOnClickListener {
            // Código para seguir el paseo en tiempo real
        }

        binding.botonRevisarPago.setOnClickListener {
            // Código para revisar el pago
        }
    }
}
