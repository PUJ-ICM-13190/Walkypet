package com.example.walkypet

data class UserUsuario(
    val name: String = "",             // Nombre del usuario
    val email: String = "",          // Correo del usuario
    val password: String = "",      // Contraseña del usuario
    var profileImageUrl: String? = null, // URL de la imagen de perfil
    val nombreMascota: String,
    val raza: String,
)