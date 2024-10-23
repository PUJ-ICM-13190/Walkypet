package com.example.walkypet

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkypet.databinding.ActivityPerfilPaseadorBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class PerfilPaseador : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPaseadorBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var uriCamera: Uri
    private lateinit var getContentCamera: ActivityResultLauncher<Uri>
    private lateinit var pictureImagePath: Uri

    private var camaraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> handleCameraResult(result) }

    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { cargarImagen(it) }
        } else {
            Toast.makeText(this, "No se selecciono imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPerfilPaseadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editarFoto.setOnClickListener {
            mostrarDialogoOpciones()
        }

        // Configurar la barra de navegación
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    // Aquí puedes cambiar de actividad o fragment si fuese necesario
                    val intent = Intent(this, PaseadorMainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    // Navegar a PerfilPaseador
                    val intent = Intent(this, PerfilPaseador::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarImagen(uri: Uri) {
        val imageStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        binding.fotoPerfil.setImageBitmap(bitmap)
    }

    private fun mostrarDialogoOpciones() {
        val opciones = arrayOf("Tomar foto", "Seleccionar de galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar foto de perfil")
        builder.setItems(opciones) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    // Logica para tomar una foto
                    requestPermisosCamara()
                }
                1 -> {
                    // Logica para seleccionar de la galería
                    val pickGalleryImage = Intent(Intent.ACTION_PICK)
                    pickGalleryImage.type = "image/*"
                    galleryActivityResultLauncher.launch(pickGalleryImage)
                }
            }
        }
        builder.show()
    }
    private fun requestPermisosCamara() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya se cuenta con los permisos", Snackbar.LENGTH_LONG).show()
                dipatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                val rationale = "Los permisos de la camara son necesarios para tomar fotos."
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }

    private fun dipatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val timeStamp: String = System.currentTimeMillis().toString() // Cambia esto para asegurarte de que el nombre sea único
            val imageFileName = "IMG_$timeStamp.jpg" // Nombres únicos para los archivos
            val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)

            pictureImagePath = FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)

            try {
                camaraActivityResultLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No se encontró la app de cámara", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se encontró la app de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            // Aquí asignamos la nueva imagen capturada a la vista
            binding.fotoPerfil.setImageURI(pictureImagePath)
        } else {
            Toast.makeText(this, "No se capturó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }


    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dipatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Permiso rechazado", Toast.LENGTH_SHORT).show()
        }
    }

}

