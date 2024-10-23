package com.example.walkypet

import android.Manifest
import android.app.UiModeManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import java.io.IOException
import java.util.*

class MapsPaseadorActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var roadManager: OSRMRoadManager
    private var roadOverlay: Polyline? = null // Evitar inicialización tardía con lateinit
    private lateinit var longPressedMarker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient // Cliente para obtener la ubicación
    private val startPoint = GeoPoint(4.627712, -74.063805) // Coordenadas de la Universidad Javeriana
    private val TAG = "MapsPaseadorActivity"

    private lateinit var geocoder: Geocoder
    private var origenLatLng: GeoPoint? = null
    private var destinoLatLng: GeoPoint? = null
    private lateinit var origenMarker: Marker
    private lateinit var destinoMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar la configuración de OSM
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContentView(R.layout.activity_maps_paseador)

        // Política de seguridad para permitir llamados síncronos (SOLO PARA PRUEBAS)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Inicializar el mapa
        map = findViewById(R.id.osmMap)
        map.setMultiTouchControls(true)

        // Inicializar el RoadManager para gestionar rutas
        roadManager = OSRMRoadManager(this, "ANDROID")

        // Inicializar el Geocoder para la búsqueda de ubicaciones
        geocoder = Geocoder(this, Locale.getDefault())

        // Inicializar FusedLocationProviderClient para obtener la ubicación actual
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar los botones de búsqueda
        val btnSearchOrigen = findViewById<Button>(R.id.btnSearchOrigen)
        val etSearchLocationOrigen = findViewById<EditText>(R.id.etSearchLocationOrigen)
        val btnSearchDestino = findViewById<Button>(R.id.btnSearchDestino)
        val etSearchLocationDestino = findViewById<EditText>(R.id.etSearchLocationDestino)
        val btnUseCurrentLocation = findViewById<Button>(R.id.btnUseCurrentLocation)

        // Listener para el botón de búsqueda de origen
        btnSearchOrigen.setOnClickListener {
            val origen = etSearchLocationOrigen.text.toString()
            if (origen.isNotEmpty()) {
                searchLocation(origen, true)
            } else {
                Toast.makeText(this, "Ingrese una dirección de origen", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón de búsqueda de destino
        btnSearchDestino.setOnClickListener {
            val destino = etSearchLocationDestino.text.toString()
            if (destino.isNotEmpty()) {
                searchLocation(destino, false)
            } else {
                Toast.makeText(this, "Ingrese una dirección de destino", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón que usa la ubicación actual como origen
        btnUseCurrentLocation.setOnClickListener {
            useCurrentLocationAsOrigin()
        }
    }

    // Obtener la ubicación actual y establecerla como el origen
    private fun useCurrentLocationAsOrigin() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = GeoPoint(location.latitude, location.longitude)
                origenLatLng = currentLocation

                // Colocar el marcador de origen en la ubicación actual
                if (::origenMarker.isInitialized) {
                    map.overlays.remove(origenMarker)
                }
                origenMarker = createMarker(currentLocation, "Origen (Mi ubicación)", null, R.drawable.location_red)
                map.overlays.add(origenMarker)

                map.controller.animateTo(currentLocation)
                map.controller.setZoom(15.0)

                // Si también se ha establecido el destino, dibujar la ruta
                if (origenLatLng != null && destinoLatLng != null) {
                    drawRoute(origenLatLng!!, destinoLatLng!!)
                }
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        val mapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
        val uiManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    // Método para buscar una ubicación usando Geocoder y marcarla en el mapa
    private fun searchLocation(query: String, isOrigen: Boolean) {
        try {
            val addresses = geocoder.getFromLocationName(query, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locationLatLng = GeoPoint(address.latitude, address.longitude)

                if (isOrigen) {
                    origenLatLng = locationLatLng
                    if (::origenMarker.isInitialized) {
                        map.overlays.remove(origenMarker)
                    }
                    origenMarker = createMarker(locationLatLng, "Origen", null, R.drawable.location_red)
                    map.overlays.add(origenMarker)
                } else {
                    destinoLatLng = locationLatLng
                    if (::destinoMarker.isInitialized) {
                        map.overlays.remove(destinoMarker)
                    }
                    destinoMarker = createMarker(locationLatLng, "Destino", null, R.drawable.location_blue)
                    map.overlays.add(destinoMarker)
                }

                map.controller.animateTo(locationLatLng)
                map.controller.setZoom(15.0)

                if (origenLatLng != null && destinoLatLng != null) {
                    drawRoute(origenLatLng!!, destinoLatLng!!)
                }
            } else {
                Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al buscar la dirección", Toast.LENGTH_SHORT).show()
        }
    }


    // Crear un nuevo marcador
    private fun createMarker(p: GeoPoint, title: String?, desc: String?, iconID: Int): Marker {
        val marker = Marker(map)
        marker.position = p
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        if (iconID != 0) {
            val myIcon = resources.getDrawable(iconID, this.theme) as BitmapDrawable
            val scaledIcon = Bitmap.createScaledBitmap(myIcon.bitmap, 80, 80, false)
            marker.icon = BitmapDrawable(resources, scaledIcon)
        }

        if (desc != null) {
            marker.subDescription = desc
        }

        return marker
    }

    // Método para dibujar una ruta entre dos puntos
    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)

        val road: Road = roadManager.getRoad(routePoints)

        if (road.mStatus != Road.STATUS_OK) {
            Toast.makeText(this, "Error al calcular la ruta", Toast.LENGTH_SHORT).show()
            return
        }

        roadOverlay?.let { map.overlays.remove(it) }

        roadOverlay = OSRMRoadManager.buildRoadOverlay(road).apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 10f
        }

        roadOverlay?.let { map.overlays.add(it) }
    }
}