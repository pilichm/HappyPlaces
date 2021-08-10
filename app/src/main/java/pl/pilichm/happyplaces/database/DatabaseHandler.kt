package pl.pilichm.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pl.pilichm.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context):
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object {
        private const val DB_NAME = "HappyPlacesDB"
        private const val DB_VERSION = 1

        private const val HAPPY_PLACES_TABLE_NAME = "HAPPY_PLACES"
        private const val COL_ID = "_ID"
        private const val COL_TITLE = "TITLE"
        private const val COL_IMAGE = "IMAGE"
        private const val COL_DESCRIPTION = "DESCRIPTION"
        private const val COL_DATE = "DATE"
        private const val COL_LOCATION = "LOCATION"
        private const val COL_LONGITUDE = "LONGITUDE"
        private const val COL_LATITUDE = "LATITUDE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $HAPPY_PLACES_TABLE_NAME ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_TITLE TEXT, " +
                "$COL_IMAGE TEXT, " +
                "$COL_DESCRIPTION TEXT, " +
                "$COL_DATE TEXT, " +
                "$COL_LOCATION TEXT, " +
                "$COL_LONGITUDE TEXT, " +
                "$COL_LATITUDE TEXT )"

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $HAPPY_PLACES_TABLE_NAME")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COL_TITLE, happyPlace.title)
        contentValues.put(COL_IMAGE, happyPlace.image)
        contentValues.put(COL_DESCRIPTION, happyPlace.description)
        contentValues.put(COL_DATE, happyPlace.date)
        contentValues.put(COL_LOCATION, happyPlace.location)
        contentValues.put(COL_LONGITUDE, happyPlace.longitude)
        contentValues.put(COL_LATITUDE, happyPlace.latitude)

        val result = db.insert(HAPPY_PLACES_TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COL_TITLE, happyPlace.title)
        contentValues.put(COL_IMAGE, happyPlace.image)
        contentValues.put(COL_DESCRIPTION, happyPlace.description)
        contentValues.put(COL_DATE, happyPlace.date)
        contentValues.put(COL_LOCATION, happyPlace.location)
        contentValues.put(COL_LONGITUDE, happyPlace.longitude)
        contentValues.put(COL_LATITUDE, happyPlace.latitude)

        val result = db.update(
            HAPPY_PLACES_TABLE_NAME,
            contentValues,
            "$COL_ID = ${happyPlace.id}",
            null)
        db.close()
        return result
    }

    fun getHappyPlacesList(): ArrayList<HappyPlaceModel>{
        val happyPlaceList = ArrayList<HappyPlaceModel>()

        val query = "SELECT * FROM $HAPPY_PLACES_TABLE_NAME"
        val db = this.readableDatabase

        try {
            val cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()){
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(COL_ID)),
                        cursor.getString(cursor.getColumnIndex(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COL_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COL_DATE)),
                        cursor.getString(cursor.getColumnIndex(COL_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE))
                    )

                    happyPlaceList.add(place)
                } while(cursor.moveToNext())

                cursor.close()
            }
        } catch (e: SQLiteException){
            e.printStackTrace()
        } finally {
            Log.i("DatabaseHandler-SIZE", "${happyPlaceList.size}")
            return happyPlaceList
        }
    }

}