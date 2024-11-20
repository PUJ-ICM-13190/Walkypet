package com.example.walkypet

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import java.util.*

class TrackingActivity : AppCompatActivity() {

    private lateinit var trackingMap: MapView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var roadManager: OSRMRoadManager
    private var currentRoute: Polyline? = null
    private var currentMarker: Marker? = null

    private var origenLatLng: GeoPoint? = null
    private var destinoLatLng: GeoPoint? = null

    private var previousLocation: GeoPoint? = null // Variable para almacenar la ubicación anterior
    private val handler = Handler()
    private val updateInterval: Long = 10000 // 10 segundos

    private val locationRunnable = object : Runnable {
        override fun run() {
            setupCurrentLocation()
            handler.postDelayed(this, updateInterval) // Repetir cada 10 segundos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la configuración de OSM
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_tracking)

        trackingMap = findViewById(R.id.trackingMap)
        trackingMap.setMultiTouchControls(true)


        // Inicializar Base de Datos
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        roadManager = OSRMRoadManager(this, "ANDROID")

        // Recuperar las coordenadas de origen y destino del Intent
        val latDestino = intent.getDoubleExtra("DESTINO_LAT", 0.0)
        val lngDestino = intent.getDoubleExtra("DESTINO_LNG", 0.0)
        val latOrigen = intent.getDoubleExtra("ORIGEN_LAT", 0.0)
        val lngOrigen = intent.getDoubleExtra("ORIGEN_LNG", 0.0)

        if (latDestino != 0.0 && lngDestino != 0.0) {
            destinoLatLng = GeoPoint(latDestino, lngDestino)
            addMarker(destinoLatLng!!, "Destino")
        }

        if (latOrigen != 0.0 && lngOrigen != 0.0) {
            origenLatLng = GeoPoint(latOrigen, lngOrigen)
            addMarker(origenLatLng!!, "Origen")
        }

        setupCurrentLocation()

        val btnStopTrip = findViewById<Button>(R.id.btnStopTrip)
        btnStopTrip.setOnClickListener {
            stopTracking()
        }

        // Iniciar la actualización periódica
        handler.postDelayed(locationRunnable, updateInterval)
    }

    private fun setupCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val userId = auth.currentUser?.uid
        if (userId != null) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                    moveToLocation(currentGeoPoint)
                    addMarker(currentGeoPoint, "Ubicación Actual")

                    if (origenLatLng != null && destinoLatLng != null) {
                        drawRoute(origenLatLng!!, destinoLatLng!!)
                    }

                    // Dibujar una línea verde entre la ubicación anterior y la actual
                    previousLocation?.let {
                        drawMovementLine(it, currentGeoPoint)
                    }

                    // Actualizar la ubicación en Firestore
                    val userUpdates = hashMapOf(
                        "geo" to currentGeoPoint
                    )

                    firestore.collection("usersPaseador").document(userId).update(userUpdates as Map<String, Any>)
                        .addOnSuccessListener {
                            // Handle success
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar ubicación: ${e.message}", Toast.LENGTH_LONG).show()
                        }

                    // Actualizar la ubicación anterior
                    previousLocation = currentGeoPoint
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToLocation(location: GeoPoint) {
        trackingMap.controller.setZoom(15.0)
        trackingMap.controller.setCenter(location)
    }

    private fun addMarker(location: GeoPoint, title: String?) {
        currentMarker?.let { trackingMap.overlays.remove(it) }
        val marker = Marker(trackingMap)
        marker.position = location
        marker.title = title ?: "Ubicación sin título"  // Default title if null
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        trackingMap.overlays.add(marker)
        currentMarker = marker
    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)

        val road: Road = roadManager.getRoad(routePoints)

        if (road.mStatus != Road.STATUS_OK) {
            Toast.makeText(this, "Error al calcular la ruta", Toast.LENGTH_SHORT).show()
            return
        }

        currentRoute?.let { trackingMap.overlays.remove(it) }

        currentRoute = OSRMRoadManager.buildRoadOverlay(road).apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 10f
        }

        currentRoute?.let { trackingMap.overlays.add(it) }
    }

    private fun drawMovementLine(start: GeoPoint, finish: GeoPoint) {
        val movementLine = Polyline()
        movementLine.setPoints(listOf(start, finish))
        movementLine.outlinePaint.color = Color.GREEN
        movementLine.outlinePaint.strokeWidth = 5f
        trackingMap.overlays.add(movementLine)
    }

    private fun stopTracking() {
        Toast.makeText(this, "Viaje detenido", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(locationRunnable) // Detener la actualización periódica
        finish() // Regresa a la actividad anterior
    }

    override fun onResume() {
        super.onResume()
        trackingMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        trackingMap.onPause()
        handler.removeCallbacks(locationRunnable) // Detener la actualización al pausar
    }
}
