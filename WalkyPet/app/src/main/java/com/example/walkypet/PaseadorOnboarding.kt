package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkypet.databinding.ActivityPaseadorOnboardingBinding

class PaseadorOnboarding : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaseadorOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonSignIn.setOnClickListener {
            // Lógica para iniciar sesión
            val intent = Intent(this, PaseadorMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textViewSignUp.setOnClickListener {
            // Redirigir a la página de registro
            val intent = Intent(this, PaseadorSignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
