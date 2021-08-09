package pl.pilichm.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_happy_place_detail.*
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)

        var happyPlaceItem: HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACES_DETAILS)){
            happyPlaceItem = intent.getSerializableExtra(
                MainActivity.EXTRA_PLACES_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceItem!=null){
            setSupportActionBar(toolbarHappyPlacesDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceItem.title
            toolbarHappyPlacesDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            ivPlaceImageDetail.setImageURI(Uri.parse(happyPlaceItem.image))
            tvDescriptionDetail.text = happyPlaceItem.description
            tvLocationDetail.text = happyPlaceItem.location
        }
    }
}