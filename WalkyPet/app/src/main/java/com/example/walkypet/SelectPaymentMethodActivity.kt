package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivitySelectPaymentMethodBinding

class SelectPaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonConfirmPaymentMethod.setOnClickListener {
            val selectedPaymentMethodId = binding.radioGroupPaymentMethods.checkedRadioButtonId
            if (selectedPaymentMethodId != -1) {
                val intent = Intent(this, ReservationSummaryActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, selecciona un método de pago", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
