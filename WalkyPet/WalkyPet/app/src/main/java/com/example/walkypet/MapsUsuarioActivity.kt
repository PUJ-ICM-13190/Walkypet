package com.example.walkypet

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.google.android.gms.maps.model.CameraPosition
import kotlin.math.abs
import kotlin.math.sqrt

class MapsUsuarioActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsUsuarioBinding
    private var marker: Marker? = null
    private lateinit var polyline: Polyline
    private val handler = Handler()
    private var currentIndex = 0
    private var delayTime = 2000L  // Tiempo de retardo inicial (2 segundos)

    // SensorManager y sensores
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // Coordenadas predefinidas para la ruta
    private val simonBolivarRuta = listOf(
        LatLng(4.656480, -74.093910),
        LatLng(4.657670, -74.092340),
        LatLng(4.659090, -74.093080),
        LatLng(4.660050, -74.092800),
        LatLng(4.660150, -74.092300),
        LatLng(4.663340, -74.094270),
        LatLng(4.660730, -74.095900),
        LatLng(4.659140, -74.096500)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Registrar los sensores
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Inicializar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val parqueSimonBolivar = LatLng(4.658945, -74.093908)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parqueSimonBolivar, 16f))

        marker = mMap.addMarker(MarkerOptions().position(parqueSimonBolivar).title("Paseador en camino"))

        // Dibujar la ruta
        drawRouteLine(simonBolivarRuta)

        // Iniciar la simulación del paseo
        simulateWalk()
    }

    private fun drawRouteLine(ruta: List<LatLng>) {
        val routePolylineOptions = PolylineOptions()
            .addAll(ruta)
            .color(Color.BLUE)
            .width(10f)

        polyline = mMap.addPolyline(routePolylineOptions)
    }

    private fun simulateWalk() {
        handler.postDelayed({
            if (currentIndex < simonBolivarRuta.size) {
                marker?.position = simonBolivarRuta[currentIndex]
                marker?.position?.let { CameraUpdateFactory.newLatLng(it) }?.let { mMap.moveCamera(it) }

                currentIndex++
                simulateWalk()
            } else {
                Toast.makeText(this, "El paseador ha llegado al destino.", Toast.LENGTH_LONG).show()
            }
        }, delayTime)  // El tiempo de retardo depende del sensor
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_GYROSCOPE -> handleGyroscope(event)
        }
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Modificar el tiempo de retardo (velocidad del paseo) según la magnitud del acelerómetro
        val accelerationMagnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        delayTime = if (accelerationMagnitude > 10) {
            1000L  // Acelera el paseo si la magnitud es mayor a 10
        } else {
            2000L  // Mantén la velocidad normal
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        val rotationRateX = event.values[0]
        val rotationRateY = event.values[1]
        val rotationRateZ = event.values[2]

        // Umbrales para detectar rotación significativa en los ejes
        val rotationThreshold = 0.5f

        // Si se detecta una rotación significativa en el eje Z (girar el dispositivo en sentido horario/anti-horario)
        if (abs(rotationRateZ) > rotationThreshold) {
            // Cambiar la dirección de la cámara en función de la rotación detectada
            mMap.animateCamera(CameraUpdateFactory.scrollBy(-rotationRateZ * 10, 0F))  // Ajustar el valor multiplicador si es necesario
        }

        // Si se detecta una rotación significativa en el eje X (inclinar el dispositivo hacia adelante/atrás)
        if (abs(rotationRateX) > rotationThreshold) {
            // Cambiar el ángulo de inclinación de la cámara (tilt) según la inclinación del dispositivo
            val cameraPosition = mMap.cameraPosition
            val newTilt = cameraPosition.tilt + (rotationRateX * 5)  // Ajustar el valor multiplicador si es necesario
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder(cameraPosition)
                    .tilt(newTilt.coerceIn(0f, 90f))  // Mantener el tilt dentro de un rango aceptable
                    .build()
            ))
        }

        // Si se detecta una rotación significativa en el eje Y (girar el dispositivo hacia arriba/abajo)
        if (abs(rotationRateY) > rotationThreshold) {
            // Zoom in o zoom out según la rotación en el eje Y
            val zoomChange = if (rotationRateY > 0) 0.1f else -0.1f
            mMap.animateCamera(CameraUpdateFactory.zoomBy(zoomChange))
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar, pero es parte de la interfaz
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)  // Detener los sensores cuando la actividad está en pausa
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }
}
