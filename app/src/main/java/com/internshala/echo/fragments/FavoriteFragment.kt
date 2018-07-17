package com.internshala.echo.fragments
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.internshala.echo.CurrentSongHelper
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.adapters.FavoriteAdapter
import com.internshala.echo.databases.EchoDatabase
/**
 * A simple [Fragment] subclass.
 */

class FavoriteFragment : Fragment() {

    var myActivity: Activity? = null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0


    var favoriteContent: EchoDatabase? = null


    var refreshList: ArrayList<Songs>? = null

    var getListfromDatabase: ArrayList<Songs> ?= null
    var pro:TextView?=null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
        var flag:Int=0
        var MY_PREFS_THEME="darkTheme"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      activity?.title="Favorites"
        setHasOptionsMenu(true)
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        favoriteContent = EchoDatabase(myActivity)
        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        recyclerView = view.findViewById(R.id.favoriteRecycler)
        pro=view.findViewById(R.id.pro)
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        display_favorites_by_searching()
        bottomBarSetup()

        var pre=myActivity?.getSharedPreferences(Statified.MY_PREFS_THEME,Context.MODE_PRIVATE)
        var isDark=pre?.getBoolean("feature",false)
        if(isDark as Boolean)
        {
            Statified.flag=1
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }


    fun getSongsFromPhone(): ArrayList<Songs>? {
        var arrayList = ArrayList<Songs>()


        var contentResolver = myActivity?.contentResolver


        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var songCursor = contentResolver?.query(songUri, null, null, null, null)


        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {

                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)


                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        } else {
            return null
        }


        return arrayList
    }

   fun bottomBarSetup() {
        try {

            bottomBarClickHandler()

            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)

           SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                SongPlayingFragment.Staticated.onSongComplete()
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)

            })


            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }

             } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {

          nowPlayingBottomBar?.setOnClickListener({

            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()

            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.sp?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)

            args.putString("FavBottomBar", "success")

              songPlayingFragment.arguments = args

            fragmentManager?.beginTransaction()
                    ?.replace(R.id.details_fragment, songPlayingFragment)

                  ?.addToBackStack("SongPlayingFragment")
                    ?.commit()
        })

          playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {

               SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {

                 SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }
var idList:ArrayList<Int>?=null

     fun display_favorites_by_searching() {

        if (favoriteContent?.checkSize() as Int > 0) {

            refreshList = ArrayList<Songs>()
   val fetchListfromDevice = getSongsFromPhone()

             if (fetchListfromDevice != null) {



                for (i in 0..fetchListfromDevice?.size - 1) {


                    var come:Int= favoriteContent?.queryDBList(fetchListfromDevice.get(i)?.songID )!!
                    if(come==1)
                    {
                        refreshList!!.add(fetchListfromDevice.get(i))
                    }
                }
            } else {
            }

             if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {

                val favoriteAdapter = FavoriteAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {

            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }
    }
}