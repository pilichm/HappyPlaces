package pl.pilichm.happyplaces.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.pilichm.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import pl.pilichm.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var happyPlaceItem: HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACES_DETAILS)){
            happyPlaceItem = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACES_DETAILS)
        }

        if (happyPlaceItem!=null){
            setSupportActionBar(binding.toolbarHappyPlacesDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceItem.title
            binding.toolbarHappyPlacesDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.ivPlaceImageDetail.setImageURI(Uri.parse(happyPlaceItem.image))
            binding.tvDescriptionDetail.text = happyPlaceItem.description
            binding.tvLocationDetail.text = happyPlaceItem.location

            binding.btnViewOnMap.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACES_DETAILS, happyPlaceItem)
                startActivity(intent)
            }
        }
    }
}