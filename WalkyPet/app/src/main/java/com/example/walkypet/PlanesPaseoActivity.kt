package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPlanesPaseoBinding

class PlanesPaseoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanesPaseoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanesPaseoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar botones para los diferentes planes
        binding.buttonSeleccionarPlanBasico.setOnClickListener {
            startPaymentMethodActivity()
        }

        binding.buttonSeleccionarPlanSecundario.setOnClickListener {
            startPaymentMethodActivity()
        }

        binding.buttonSeleccionarPlanPremium.setOnClickListener {
            startPaymentMethodActivity()
        }
    }

    private fun startPaymentMethodActivity() {
        val intent = Intent(this, SelectPaymentMethodActivity::class.java)
        startActivity(intent)
    }
}


