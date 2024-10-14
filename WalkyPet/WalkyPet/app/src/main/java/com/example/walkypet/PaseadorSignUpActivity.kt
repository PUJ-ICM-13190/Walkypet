package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseadorSignUpBinding

class PaseadorSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseadorSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            // Lógica para registrar al paseador y redirigir a la página principal
            val intent = Intent(this, PaseadorMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
