package pl.pilichm.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.adapters.HappyPlacesAdapter
import pl.pilichm.happyplaces.database.DatabaseHandler
import pl.pilichm.happyplaces.models.HappyPlaceModel
import pl.pilichm.happyplaces.utils.SwipeToDeleteCallback
import pl.pilichm.happyplaces.utils.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ADD_PLACE_ACTIVITY_RESULT_CODE = 1
        const val EXTRA_PLACES_DETAILS = "extra_place_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, AddHappyPlaceActivity::class.java),
                ADD_PLACE_ACTIVITY_RESULT_CODE
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

        adapter.setOnClickListener(object: HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, item: HappyPlaceModel) {
                val intent = Intent(applicationContext, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACES_DETAILS, item)
                startActivity(intent)
            }
        })

        rvHappyPlacesList.setHasFixedSize(true)
        rvHappyPlacesList.adapter = adapter

        /**
         * Swipe helper for updating items - on swipe right.
         */
        val editSwipeHandler = object: SwipeToEditCallback(applicationContext){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_RESULT_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)

        /**
         * Swipe helper for deleting items - on swipe left.
         */
        val deleteSwipeHandler = object: SwipeToDeleteCallback(applicationContext){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.notifyDeleteItem(
                    this@MainActivity, viewHolder.adapterPosition)
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==ADD_PLACE_ACTIVITY_RESULT_CODE){
            if (resultCode==Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            } else {
                Log.i("MainActivity", "Cancelled or back pressed.")
            }
        }
    }
}