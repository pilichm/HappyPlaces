package pl.pilichm.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.database.DatabaseHandler
import pl.pilichm.happyplaces.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        if (intent.hasExtra(MainActivity.EXTRA_PLACES_DETAILS)){
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACES_DETAILS)
        }

        setUpActionBar()
        setUpDialogPicker()
        updateDateInView()

        if (mHappyPlaceDetails!=null){
            supportActionBar?.title = "Edit Happy Place"

            etTitle.setText(mHappyPlaceDetails!!.title)
            etDescription.setText(mHappyPlaceDetails!!.description)
            etDate.setText(mHappyPlaceDetails!!.date)
            etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude
            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            ivPlaceImage.setImageURI(saveImageToInternalStorage)
            btnSave.text = "UPDATE"
        }

        tvAddImage.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ADD HAPPY PLACE"
        toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpDialogPicker(){
        dateSetListener = DatePickerDialog.OnDateSetListener{
                _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        etDate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.etDate -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tvAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select action")
                val pictureDialogItems = arrayOf("Select photo from gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                        _, which ->
                        when (which) {
                            0 -> choosePhotoFromGallery()
                            1 -> takePhotoWithCamera()
                        }
                }
                pictureDialog.show()
            }
            R.id.btnSave -> {
                when {
                    etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Please enter a title!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Please enter a description!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Please enter a location!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(
                            applicationContext,
                            "Please select an image!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val happyPlace = HappyPlaceModel(
                            if (mHappyPlaceDetails==null) 0 else mHappyPlaceDetails!!.id,
                            etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            etDescription.text.toString(),
                            etDate.text.toString(),
                            etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val dbHandler = DatabaseHandler(applicationContext)

                        if (mHappyPlaceDetails==null){
                            val result = dbHandler.addHappyPlace(happyPlace)
                            if (result>0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            val result = dbHandler.updateHappyPlace(happyPlace)
                            if (result>0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val intent = Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun takePhotoWithCamera(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun updateDateInView(){
        etDate.setText("${calendar.get(Calendar.DAY_OF_MONTH)}" +
                ".${calendar.get(Calendar.MONTH)}" +
                ".${calendar.get(Calendar.YEAR)}")
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It look like you have turned off permission " +
                "required for this feature. It ca be enabled in application settings.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                if (data!=null){
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.contentResolver, contentUri)
                        ivPlaceImage.setImageBitmap(selectedImageBitmap)
                        saveImageToInternalStorage = storeImageToInternalStorage(selectedImageBitmap)
                        Log.i("AddHappyPlaceActivity", saveImageToInternalStorage.toString())
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            applicationContext,
                            "Failed to load image from gallery!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (requestCode == CAMERA){
                val image: Bitmap = data!!.extras!!.get("data") as Bitmap
                ivPlaceImage.setImageBitmap(image)
                saveImageToInternalStorage = storeImageToInternalStorage(image)
                Log.i("AddHappyPlaceActivity", saveImageToInternalStorage.toString())
            }
        }
    }

    private fun storeImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIR = "HAppyPlacesImages"
    }
}