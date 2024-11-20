package com.example.walkypet

import android.os.Parcel
import android.os.Parcelable

// Implementar Parcelable
data class Paseador(
    val nombre: String,
    val raza: String,
    val horario: String,
    val latitud: Double? = null, // Latitud (opcional por defecto)
    val longitud: Double? = null // Longitud (opcional por defecto)
) : Parcelable {
    // Constructor que recibe un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(raza)
        parcel.writeString(horario)
        parcel.writeDouble(latitud ?: 0.0) // Si la latitud es null, se asigna 0.0
        parcel.writeDouble(longitud ?: 0.0) // Si la longitud es null, se asigna 0.0
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Paseador> {
        override fun createFromParcel(parcel: Parcel): Paseador {
            return Paseador(parcel)
        }

        override fun newArray(size: Int): Array<Paseador?> {
            return arrayOfNulls(size)
        }
    }
}
