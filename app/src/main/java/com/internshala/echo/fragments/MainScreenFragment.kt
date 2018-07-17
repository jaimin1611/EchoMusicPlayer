package com.internshala.echo.fragments


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.adapters.MainScreenAdapter
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainScreenFragment : Fragment() {

    var getSongsList:ArrayList<Songs>?=null;
    var nowPlayingBottomBar : RelativeLayout?=null
    var playPauseButton:ImageButton?=null
    var songTitle:TextView?=null
    var visibleLayout:RelativeLayout?=null;
    var noSongs:RelativeLayout?=null
    var recyclerView:RecyclerView?=null
    var myActivity:Activity?=null
    var _mainScreenAdapter:MainScreenAdapter?=null
    var trackPosition:Int=0
    var tr:TextView?=null
    object Statified {
var MY_PREFS_THEME="darkTheme"
        var flag:Int?=0
        var mediaPlayer: MediaPlayer? = null
        var tlbar:Toolbar?=null
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

activity?.title="All Songs"
        val view = inflater!!.inflate(R.layout.fragment_main_screen,container,false)
        setHasOptionsMenu(true)
        nowPlayingBottomBar=view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        playPauseButton=view?.findViewById<ImageButton>(R.id.playPauseButton)
        songTitle=view?.findViewById<TextView>(R.id.songTitleMainScreen)
        visibleLayout=view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs=view?.findViewById<RelativeLayout>(R.id.noSongs)
        recyclerView=view?.findViewById<RecyclerView>(R.id.contentMain)
        tr=view?.findViewById(R.id.trackTitle)
Statified.tlbar=view?.findViewById(R.id.toolbar)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var pre=myActivity?.getSharedPreferences(Statified.MY_PREFS_THEME,Context.MODE_PRIVATE)
        var isDark=pre?.getBoolean("feature",false)
        if(isDark as Boolean)
        {
            Statified.flag=1
        }

        var pre1=myActivity?.getSharedPreferences("shakeFeature",Context.MODE_PRIVATE)
        var isDark1=pre1?.getBoolean("feature",false)
        if(isDark1 as Boolean)
        {
            SongPlayingFragment.Statified.shakeflag=1
        }

      bottomBarSetup()
        getSongsList=getSongsFromPhone()
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if(getSongsList==null)
        {
            visibleLayout?.visibility=View.INVISIBLE
            noSongs?.visibility=View.VISIBLE
        }
        else{
        _mainScreenAdapter= MainScreenAdapter(getSongsList as ArrayList<Songs>,myActivity as Context)
        val mLayoutManager=LinearLayoutManager(myActivity)
        recyclerView?.layoutManager=mLayoutManager
        recyclerView?.itemAnimator=DefaultItemAnimator()
        recyclerView?.adapter=_mainScreenAdapter}
        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        var switcher=item?.itemId
        if(switcher==R.id.action_sort_acsending)
        {
            if(getSongsList!=null)
            { val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                editor?.putString("action_sort_ascending", "true")
                editor?.putString("action_sort_recent", "false")
                editor?.apply()
                Collections.sort(getSongsList,Songs.Statified.nameComparator)

            }
            _mainScreenAdapter?.notifyDataSetChanged()

        }else if(switcher==R.id.action_sort_recent)
        {
            val editortwo = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent", "true")
            editortwo?.putString("action_sort_ascending", "false")
            editortwo?.apply()
            if(getSongsList!=null)
            {
                Collections.sort(getSongsList,Songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()

        }
        return super.onOptionsItemSelected(item)




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

           MainScreenFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()

             args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.sp?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)

             args.putString("MainBottomBar", "success")

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
    fun getSongsFromPhone():ArrayList<Songs>{

        var arrayList=ArrayList<Songs>()
        var contentResolver=myActivity?.contentResolver
        var songUri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor=contentResolver?.query(songUri,null,null,null,null)
        if(songCursor!=null && songCursor.moveToFirst())
        {
            val songId=songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex=songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while(songCursor.moveToNext())
            {
                var currentId=songCursor.getLong(songId)
                var currentTitle=songCursor.getString(songTitle)
                var currentArtists=songCursor.getString(songArtist)
                var currentData=songCursor.getString(songData)
                var currentDate=songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId,currentTitle,currentArtists,currentData,currentDate))
            }

        }
        return arrayList
    }


}
