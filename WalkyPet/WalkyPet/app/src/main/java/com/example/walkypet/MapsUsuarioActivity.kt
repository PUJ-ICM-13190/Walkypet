package com.example.walkypet

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.example.walkypet.databinding.ActivityMapsUsuarioBinding
import android.graphics.Color

class MapsUsuarioActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsUsuarioBinding
    private var marker: Marker? = null
    private lateinit var polyline: Polyline  // Para manejar la ruta dibujada
    private val handler = Handler()  // Handler para actualizar la posición en tiempo real
    private var currentIndex = 0  // Índice para simular el progreso del paseo

    // Coordenadas predefinidas para la ruta en el Parque Metropolitano Simón Bolívar
    private val simonBolivarRuta = listOf(
        LatLng(4.656480, -74.093910),  // Punto 1
        LatLng(4.657670, -74.092340),  // Punto 2
        LatLng(4.659090, -74.093080),  // Punto 3
        LatLng(4.660050, -74.092800),  // Punto 4
        LatLng(4.660150, -74.092300),  // Punto 4
        LatLng(4.663340, -74.094270),  // Punto 5
        LatLng(4.660730, -74.095900),  // Punto 6
        LatLng(4.659140, -74.096500),  // Punto 7
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene el fragmento de soporte de mapas
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Mover la cámara a la ubicación del Parque Metropolitano Simón Bolívar
        val parqueSimonBolivar = LatLng(4.658945, -74.093908)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parqueSimonBolivar, 16f))

        // Colocar un marcador en el punto de inicio de la ruta
        marker = mMap.addMarker(MarkerOptions().position(parqueSimonBolivar).title("Paseador en camino"))

        // Dibujar la línea que representa la ruta
        drawRouteLine(simonBolivarRuta)

        // Iniciar la simulación del paseo
        simulateWalk()
    }

    // Función para dibujar la línea de la ruta que debe seguir el paseador
    private fun drawRouteLine(ruta: List<LatLng>) {
        val routePolylineOptions = PolylineOptions()
            .addAll(ruta)
            .color(Color.BLUE)  // Color de la ruta
            .width(10f)         // Ancho de la línea

        // Añadir la línea al mapa
        polyline = mMap.addPolyline(routePolylineOptions)
    }

    // Función para simular el movimiento del paseador en tiempo real
    private fun simulateWalk() {
        handler.postDelayed({
            if (currentIndex < simonBolivarRuta.size) {
                // Mover el marcador a la siguiente coordenada de la ruta
                marker?.position = simonBolivarRuta[currentIndex]
                marker?.position?.let { CameraUpdateFactory.newLatLng(it) }
                    ?.let { mMap.moveCamera(it) }

                // Actualizar el índice para la siguiente posición
                currentIndex++

                // Continuar el paseo mientras queden puntos
                simulateWalk()
            } else {
                // Mostrar un mensaje de que se ha llegado al destino
                Toast.makeText(this, "El paseador ha llegado al destino.", Toast.LENGTH_LONG).show()
            }
        }, 2000)  // Simular movimiento cada 2 segundos
    }
}