package com.example.walkypet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlin.math.abs
import kotlin.math.sqrt
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.maps.model.CameraPosition

class MapsUsuarioActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsUsuarioBinding
    private var marker: Marker? = null
    private lateinit var polyline: Polyline
    private val handler = Handler()
    private var currentIndex = 0
    private var delayTime = 200L  // Tiempo de retardo ajustado para más fluidez

    // SensorManager y sensores
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // Coordenadas predefinidas para la ruta (más cercanas para un movimiento más suave)
    private val simonBolivarRuta = listOf(
        LatLng(4.656480, -74.093910),
        LatLng(4.656620, -74.093850),
        LatLng(4.656760, -74.093790),
        LatLng(4.656900, -74.093730),
        LatLng(4.657050, -74.093670),
        LatLng(4.657200, -74.093600),
        LatLng(4.657350, -74.093530),
        LatLng(4.657500, -74.093460),
        LatLng(4.657650, -74.093390),
        LatLng(4.657800, -74.093320),
        LatLng(4.657950, -74.093250),
        LatLng(4.658100, -74.093180)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar y solicitar permisos de notificación en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Inicializar el SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Registrar los sensores
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Inicializar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        createNotificationChannel()  // Crear canal para notificaciones
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
                // Actualizar la posición del marcador
                marker?.position = simonBolivarRuta[currentIndex]
                marker?.position?.let { CameraUpdateFactory.newLatLng(it) }?.let { mMap.moveCamera(it) }

                currentIndex++

                // Llamar a la función para simular el siguiente paso
                simulateWalk()
            } else {
                // Una vez que se llega al final, se envía la notificación
                sendPushNotification()
            }
        }, delayTime)  // El tiempo de retardo ajustado para mayor fluidez
    }

    private fun sendPushNotification() {
        // Verificar si el permiso de notificación está concedido
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = Notification.Builder(this, "walk_notification")
                .setContentTitle("Paseo finalizado")
                .setContentText("El paseador ha llegado al destino.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Icono para la notificación
                .build()

            notificationManager.notify(1, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Walk Notifications"
            val descriptionText = "Notificaciones sobre el progreso del paseo"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("walk_notification", name, importance).apply {
                description = descriptionText
            }

            // Registrar el canal
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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

        // Reducir la abruptitud del cambio de velocidad
        delayTime = when {
            accelerationMagnitude > 15 -> 100L  // Acelera el paseo si la magnitud es mayor a 15
            accelerationMagnitude > 10 -> 150L  // Velocidad media
            else -> 200L  // Velocidad más lenta para movimientos suaves
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        val rotationRateX = event.values[0]
        val rotationRateY = event.values[1]
        val rotationRateZ = event.values[2]

        // Umbrales para detectar rotación significativa en los ejes
        val rotationThreshold = 0.5f

        // Si se detecta una rotación significativa en el eje Z (girar el dispositivo)
        if (abs(rotationRateZ) > rotationThreshold) {
            mMap.animateCamera(CameraUpdateFactory.scrollBy(-rotationRateZ * 10, 0F))
        }

        // Si se detecta una rotación significativa en el eje X (inclinación del dispositivo)
        if (abs(rotationRateX) > rotationThreshold) {
            val cameraPosition = mMap.cameraPosition
            val newTilt = cameraPosition.tilt + (rotationRateX * 5)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder(cameraPosition)
                    .tilt(newTilt.coerceIn(0f, 90f))
                    .build()
            ))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }
}
