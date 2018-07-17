package com.internshala.echo.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.internshala.echo.Songs

class EchoDatabase:SQLiteOpenHelper {


    var _songList:ArrayList<Songs>?=null

    object Staticated{
        val DB_NAME="FavoriteDatabase"
        val TABLE_NAME="FavoriteTable"
        val COLUMN_ID="SongID"
        val COLUMN_SONG_TITLE="SongTitle"
        val COLUMN_SONG_ARTIST="SongArtist"
        val COLUMN_SONG_PATH="SongPath"

        var DB_VERSION=1
    }
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID +
                " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING,"
                + Staticated.COLUMN_SONG_PATH + " STRING);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version) {}
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION) {}
    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {
            val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(Staticated.COLUMN_ID, id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)

        db.insert(Staticated.TABLE_NAME,null,contentValues)
        db.close()

    }


    /*fun queryDBList():ArrayList<Songs>?{

        try{
            val db=this.readableDatabase
            val query_params="SELECT * FROM "+Staticated.TABLE_NAME
            var cSor=db.rawQuery(query_params,null)
            if(cSor.moveToFirst())
            {
                do{
                    var _id=cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist=cSor.getString((cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST)))
                    var _title=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songPath=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList?.add(Songs(_id.toLong(),_title,_artist,_songPath,0))
                }
                    while (cSor.moveToNext())
            }
            else
            {

                return null

            }


        }catch(e:Exception)
        {
            e.printStackTrace()
        }
return _songList
    }*/

    fun queryDBList(i:Long): Int?
    {
var flag=0
        /*Here a try-catch block is used to handle the exception as no songs in the database can result in null-pointer exception*/

        try {
            val db = this.readableDatabase

            /*The SQL query used for obtaining the songs is :
            * SELECT * FROM FavoriteTable
            * The query returns all the items present in the table*/
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)

            /*The cSor stores the result obtained from the database
            * The function moveToFirst() checks if there are any entries or not*/
            if (cSor.moveToFirst()) {

                /*If 1 or more rows are returned then we store all the entries into the array list _songList*/
                do {

                    var _id = cSor.getLong(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    if(_id==i)
                    {
                        flag=1
                    }
                }

                /*This task is performed till there are items present*/
                while (cSor.moveToNext())
            }

            /*Otherwise null is returned*/
            else {
                return 0
            }
        }

        /*If there was any exception then it is handled by this*/
        catch (e: Exception) {
            e.printStackTrace()
        }

        /*Finally we return the songList which contains the songs present inside the database*/
        return flag
    }


    fun checkifIdExists(_id:Int):Boolean{
        var storeId=-1000
        val db=this.readableDatabase
        var query_params="SELECT * FROM "+Staticated.TABLE_NAME+" WHERE SongId='$_id';"
        var cSor=db.rawQuery(query_params,null)
        if(cSor.moveToFirst()){
            do{
                storeId=cSor.getInt((cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID)))
            }
                while (cSor.moveToNext())
        }
        else
        {
            return false
        }
        return storeId!=-1000
    }


    fun deleteFavorite(_id:Int){
        var db=this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID +"="+_id,null)
        db.close()
    }


    fun checkSize():Int{
        var counter=0
        val db=this.readableDatabase
        var quert_param="SELECT * FROM "+Staticated.TABLE_NAME
        val cSor=db.rawQuery(quert_param,null)
        if(cSor.moveToFirst())
        {
            do{
            counter=counter+1}
                while(cSor.moveToNext())
        }
        else
        {
            return 0
        }
        return counter

    }
}
