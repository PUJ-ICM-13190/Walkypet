package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseadorDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaseadorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseadorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Obtener el ID del paseador seleccionado desde el Intent
        val selectedUserDocId = intent.getStringExtra("selectedUserDocId") ?: return

        // Cargar los detalles del paseador
        getPaseadorDetails(selectedUserDocId)

        // Acción al presionar "Agendar Paseo"
        binding.buttonAgendarPaseo.setOnClickListener {
            val intent = Intent(this, PlanesPaseoActivity::class.java)
            intent.putExtra("paseadorDocId", selectedUserDocId)  // Pasar el ID del paseador
            startActivity(intent)
        }
    }

    // Método para obtener los detalles del paseador desde Firestore
    private fun getPaseadorDetails(selectedUserDocId: String) {
        // Verificar si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Escuchar cambios en los detalles del paseador en Firestore
        firestore.collection("usersPaseador").document(selectedUserDocId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val name = it.getString("name")
                    val raza = it.getString("raza")
                    val horario = it.getString("horario")
                    val descripcion = it.getString("descripcion")
                    val telefono = it.getString("telefono")

                    // Si los datos están disponibles, mostrar en la UI
                    if (name != null && raza != null && horario != null) {
                        binding.textViewPaseadorName.text = name
                        binding.textViewPaseadorRazas.text = "Razas: $raza"
                        binding.textViewPaseadorHorario.text = "Horario: $horario"

                    } else {
                        Toast.makeText(this, "Datos del paseador no disponibles", Toast.LENGTH_SHORT).show()
                    }
                }
                // Manejo de error si no se encuentra el documento
                    ?: Toast.makeText(this, "Paseador no encontrado", Toast.LENGTH_SHORT).show()
            }
    }
}
