package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    lateinit var nameEdit: EditText
    lateinit var mascotaEdit: EditText
    lateinit var razaEdit: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Asociar vistas
        nameEdit = binding.editTextName
        emailEdit = binding.editTextEmail
        passEdit = binding.editTextPassword
        mascotaEdit = binding.editTextNombreMascota
        razaEdit = binding.editTextRaza  // Agregar el campo de raza

        mAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Configurar el botón de registro
        binding.registerButton.setOnClickListener {
            signUp()
        }
    }

    // Actualizar la interfaz de usuario después del inicio de sesión
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext, MainActivity3::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            emailEdit.setText("")
            passEdit.setText("")
        }
    }

    // Validar los campos del formulario
    private fun validateForm(): Boolean {
        var valid = true
        val email = emailEdit.text.toString()
        if (email.isEmpty()) {
            emailEdit.error = "Correo requerido"
            valid = false
        } else {
            emailEdit.error = null
        }

        val password = passEdit.text.toString()
        if (password.isEmpty()) {
            passEdit.error = "Contraseña requerida"
            valid = false
        } else {
            passEdit.error = null
        }

        val name = nameEdit.text.toString()
        if (name.isEmpty()) {
            nameEdit.error = "Nombre requerido"
            valid = false
        } else {
            nameEdit.error = null
        }

        return valid
    }

    // Validar si el correo tiene el formato correcto
    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }

    // Función para registrar un nuevo usuario
    private fun signUp() {
        val name = nameEdit.text.toString()
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        val mascota = mascotaEdit.text.toString()
        val raza = razaEdit.text.toString()

        // Validar el formato del correo electrónico
        if (!isEmailValid(email)) {
            Toast.makeText(this@MainActivity2, "Correo en formato no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar los campos
        if (!validateForm()) {
            return
        }

        // Crear el usuario con correo y contraseña
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    storeData(userId, name, email, pass, mascota, raza)
                }
            } else {
                Toast.makeText(this@MainActivity2, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para guardar los datos del usuario en Firestore
    private fun storeData(userId: String, name: String, email: String, password: String, mascota: String, raza: String) {
        val user = UserUsuario(
            name = name,
            email = email,
            password = password,
            nombreMascota = mascota,
            raza = raza
        )

        // Guardar los datos en la colección "usersUsuario" de Firestore
        firestore.collection("usersUsuario").document(userId).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar el usuario: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}