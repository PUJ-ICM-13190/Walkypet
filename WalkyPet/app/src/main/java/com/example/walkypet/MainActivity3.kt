package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkypet.databinding.ActivityMain3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private lateinit var paseadoresList: MutableList<String>
    private lateinit var userDocuments: MutableList<QueryDocumentSnapshot>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        paseadoresList = mutableListOf()
        userDocuments = mutableListOf()

        /* Crear algunos paseadores de ejemplo
        paseadoresList = listOf(
            Paseador("Juan", "Pequeñas", "8:00 AM - 12:00 PM"),
            Paseador("Carlos", "Medianas", "10:00 AM - 2:00 PM"),
            Paseador("Ana", "Grandes", "12:00 PM - 4:00 PM"),
            Paseador("Lucía", "Pequeñas y Medianas", "2:00 PM - 6:00 PM"),
            Paseador("Pedro", "Grandes", "4:00 PM - 8:00 PM")
        )*/

        loadPaseadores()

        /* Crear el adaptador personalizado
        val adapter = PaseadorAdapter(this, paseadoresList)
        binding.listViewPaseadores.adapter = adapter
        */
        // Configurar el ListView para que responda al clic en un paseador
        binding.listViewPaseadores.setOnItemClickListener { _, _, position, _ ->
            val selectedUserDocument = userDocuments[position]
            val intent = Intent(this, PaseadorDetailActivity::class.java)
            intent.putExtra("selectedUserDocId", selectedUserDocument.id)
            startActivity(intent)
        }

        // Configurar la barra de navegación
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    // Aquí puedes cambiar de actividad o fragment si fuese necesario
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
    private fun loadPaseadores() {
        firestore.collection("usersPaseador")
            .get()
            .addOnSuccessListener { documents ->
                paseadoresList.clear()
                userDocuments.clear()
                for (document in documents) {
                    val userName = "${document.getString("name")} (${document.getString("email")})"
                    paseadoresList.add(userName)
                    userDocuments.add(document)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, paseadoresList)
                binding.listViewPaseadores.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
