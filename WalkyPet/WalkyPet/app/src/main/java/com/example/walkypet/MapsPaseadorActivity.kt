package com.example.walkypet

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import kotlin.math.abs
import kotlin.math.atan2

class MapsPaseadorActivity : AppCompatActivity(), SensorEventListener, LocationListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private lateinit var locationManager: LocationManager

    private lateinit var map: MapView
    private val TAG = "MapsPaseadorActivity"

    private val pathPoints = mutableListOf<GeoPoint>() // Para almacenar las posiciones del mapa
    private var currentLocation: GeoPoint? = null // Almacena la ubicación actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar la configuración de OSM
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContentView(R.layout.activity_maps_paseador)

        // Inicializar el mapa
        map = findViewById(R.id.osmMap)
        map.setMultiTouchControls(true)

        // Inicializar el SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Inicializar el LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Registrar los listeners de los sensores
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }

        gyroscope?.also { gyro ->
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Comprobar y solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            startLocationUpdates() // Iniciar las actualizaciones de ubicación
        }
    }

    // Implementación de SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometerData(it)
                Sensor.TYPE_GYROSCOPE -> handleGyroscopeData(it)
                else -> {}
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                when (accuracy) {
                    SensorManager.SENSOR_STATUS_NO_CONTACT -> {
                        Log.w(TAG, "Acelerómetro: Sin contacto.")
                    }
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        Log.w(TAG, "Acelerómetro: Precisión poco confiable.")
                    }
                    else -> {
                        Log.i(TAG, "Acelerómetro: Precisión confiable.")
                    }
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                when (accuracy) {
                    SensorManager.SENSOR_STATUS_NO_CONTACT -> {
                        Log.w(TAG, "Giroscopio: Sin contacto.")
                    }
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        Log.w(TAG, "Giroscopio: Precisión poco confiable.")
                    }
                    else -> {
                        Log.i(TAG, "Giroscopio: Precisión confiable.")
                    }
                }
            }
            else -> {
                Log.d(TAG, "Sensor desconocido.")
            }
        }
    }

    private fun handleAccelerometerData(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        Log.d(TAG, "Acelerómetro - X: $x, Y: $y, Z: $z")

        // Determina si hay movimiento significativo
        if (abs(x) > 2 || abs(y) > 2) {
            if (currentLocation != null) {
                pathPoints.add(currentLocation!!) // Guardar la posición
                drawPathOnMap(currentLocation!!) // Dibuja la trayectoria en el mapa
            }
        }
    }

    private fun handleGyroscopeData(event: SensorEvent) {
        val xRotation = event.values[0]
        val yRotation = event.values[1]
        val zRotation = event.values[2]

        Log.d(TAG, "Giroscopio - X: $xRotation, Y: $yRotation, Z: $zRotation")

        // Ajusta la orientación del mapa en función de los valores de rotación
        adjustMapOrientation(xRotation.toDouble(), yRotation.toDouble(), zRotation.toDouble())
    }

    private fun startLocationUpdates() {
        // Solicitar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 10f, this)
    }

    override fun onLocationChanged(location: Location) {
        // Actualizar la ubicación actual
        currentLocation = GeoPoint(location.latitude, location.longitude)
        Log.d(TAG, "Ubicación actual - Latitud: ${location.latitude}, Longitud: ${location.longitude}")
    }

    private fun drawPathOnMap(location: GeoPoint) {
        val marker = Marker(map)
        marker.position = location
        marker.title = "Trayectoria"
        map.overlays.add(marker)
        map.controller.animateTo(location) // Centra el mapa en la nueva ubicación
    }

    private fun adjustMapOrientation(xRotation: Double, yRotation: Double, zRotation: Double) {

            // Calculamos la dirección del norte
            val northDirection = Math.toDegrees(
                atan2(yRotation,
                xRotation)
            ).toFloat()

            // Usamos el método rotate de OSMdroid para ajustar la vista del mapa
        map.mapOrientation = northDirection

            // Opcional: También puedes usar el valor de zRotation para un efecto adicional
            // Esto podría usarse para ajustar el zoom o el campo de visión
            // Ejemplo: map.zoom = Math.min(Math.max(1.0f + zRotation / 10.0f, 1.0f), 20.0f)

            Log.d(TAG, "Orientación del mapa ajustada: $northDirection")
        }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this) // Detener las actualizaciones de ubicación
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }

        // Reiniciar las actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates() // Iniciar actualizaciones si se concede el permiso
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
