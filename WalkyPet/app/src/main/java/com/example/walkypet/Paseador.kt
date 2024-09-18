package com.example.walkypet

import android.os.Parcel
import android.os.Parcelable

// Implementar Parcelable
data class Paseador(
    val nombre: String,
    val raza: String,
    val horario: String
) : Parcelable {
    // Constructor que recibe un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(raza)
        parcel.writeString(horario)
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
