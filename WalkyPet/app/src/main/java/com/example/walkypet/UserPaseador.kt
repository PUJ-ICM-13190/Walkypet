package com.example.walkypet

import com.google.firebase.firestore.GeoPoint

data class UserPaseador(
    val name: String = "",           // Nombre del paseador
    val email: String = "",          // Correo del paseador
    val password: String = "",       // Contraseña del paseador
    var profileImageUrl: String? = null, // URL de la imagen de perfil
    val raza: String,
    val horario: String,
    val geo: GeoPoint? = null,         // Geolocalización del usuario
)

data class GeoPoint(
    val latitude: Double? = null,      // Latitud
    val longitude: Double? = null       // Longitud
)
