package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityReservationSummaryBinding

class ReservationSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonConfirmReservation.setOnClickListener {
            val intent = Intent(this, ReservationConfirmationActivity::class.java)
            startActivity(intent)
            finish() // Opcional: cerrar esta actividad para que no se pueda volver con el botón atrás
        }
    }
}
