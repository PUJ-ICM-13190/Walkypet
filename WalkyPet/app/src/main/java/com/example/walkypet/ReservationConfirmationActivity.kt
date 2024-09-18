package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityReservationConfirmationBinding

class ReservationConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el mensaje de confirmación
        val reservationDetails = "¡Tu reserva ha sido confirmada con éxito!"
        binding.textViewReservationDetails.text = reservationDetails

        binding.buttonBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Cambia a la actividad principal o la que corresponda
            startActivity(intent)
            finish() // Opcional: cerrar esta actividad para que no se pueda volver con el botón atrás
        }
    }
}
