package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.walkypet.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        // Configura los click listeners para los diferentes botones
        binding.cardViewMisPaseos.setOnClickListener {
            // Navegar a la actividad de "Mis Paseos"
            val intent = Intent(this, MisPaseosActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewActualizarPerfil.setOnClickListener {
            // Navegar a la actividad de "Actualizar Perfil"
            val intent = Intent(this, ActualizarPerfilActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewPaymentMethods.setOnClickListener {
            // Navegar a la actividad de "Métodos de Pago"
            val intent = Intent(this, PaymentMethodsActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewConfiguration.setOnClickListener {
            // Navegar a la actividad de "Configuración"
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewFeedback.setOnClickListener {
            // Navegar a la actividad de "Comentarios"
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewLogout.setOnClickListener {
            // Cerrar sesión
            mAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza la actividad actual
        }
        // Configurar la barra de navegación
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
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
