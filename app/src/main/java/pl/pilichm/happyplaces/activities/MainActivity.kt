package pl.pilichm.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.adapters.HappyPlacesAdapter
import pl.pilichm.happyplaces.database.DatabaseHandler
import pl.pilichm.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace.setOnClickListener {
            startActivity(
                Intent(applicationContext, AddHappyPlaceActivity::class.java)
            )
        }

        getHappyPlacesListFromLocalDB()
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(applicationContext)
        val happyPlaces = dbHandler.getHappyPlacesList()

        if (happyPlaces.size>0){
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setUpHappyPlacesRecyclerView(happyPlaces)
        } else {
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setUpHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>){
        rvHappyPlacesList.layoutManager = LinearLayoutManager(applicationContext)
        val adapter = HappyPlacesAdapter(applicationContext, happyPlacesList)
        rvHappyPlacesList.setHasFixedSize(true)
        rvHappyPlacesList.adapter = adapter
    }
}