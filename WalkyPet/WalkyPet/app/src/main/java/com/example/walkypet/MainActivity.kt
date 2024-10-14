package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de login
        binding.button.setOnClickListener {
            val username = binding.editTextTextPersonName.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                // Aquí puedes agregar la lógica para iniciar sesión
                // Si el login es exitoso, se redirige a MainActivity3
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                finish() // Opcional: Cierra esta actividad para que el usuario no pueda volver a ella con el botón de atrás
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el botón de Facebook
        binding.facebook.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión con Facebook", Toast.LENGTH_SHORT).show()
            // Lógica para login con Facebook
        }

        // Configurar el botón de Google
        binding.google.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            // Lógica para login con Google
        }

        // Configurar el botón de "Regístrate aquí"
        binding.textView3.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        binding.textViewPaseadorOnboarding.setOnClickListener {
            val intent = Intent(this, PaseadorOnboarding::class.java)
            startActivity(intent)
        }

        // Detectar cambios en el campo de usuario
        binding.editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Puedes agregar lógica para validar el nombre de usuario
            }
        })
    }
}
