package pl.pilichm.happyplaces

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        setUpActionBar()

        dateSetListener = DatePickerDialog.OnDateSetListener{
                _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        etDate.setOnClickListener(this)

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ADD HAPPY PLACE"
        toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
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
        }
    }

    private fun updateDateInView(){
        etDate.setText("${calendar.get(Calendar.DAY_OF_MONTH)}" +
                ".${calendar.get(Calendar.MONTH)}" +
                ".${calendar.get(Calendar.YEAR)}")
    }
}