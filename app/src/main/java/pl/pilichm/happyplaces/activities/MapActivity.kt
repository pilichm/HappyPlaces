package pl.pilichm.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.databinding.ActivityMapBinding
import pl.pilichm.happyplaces.models.HappyPlaceModel

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mHappyPlaceDetail: HappyPlaceModel? = null
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(MainActivity.EXTRA_PLACES_DETAILS)){
            mHappyPlaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACES_DETAILS)
                    as HappyPlaceModel?
        }

        if (mHappyPlaceDetail!=null){
            setSupportActionBar(binding.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mHappyPlaceDetail!!.title

            binding.toolbarMap.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment

            supportMapFragment.getMapAsync(this)

        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val position = LatLng(mHappyPlaceDetail!!.latitude, mHappyPlaceDetail!!.longitude)
        googleMap!!.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetail!!.location))

        val latLongZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(latLongZoom)
    }
}