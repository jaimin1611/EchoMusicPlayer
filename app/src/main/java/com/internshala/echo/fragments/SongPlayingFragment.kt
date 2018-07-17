package com.internshala.echo.fragments
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.internshala.echo.CurrentSongHelper
import com.internshala.echo.R
import com.internshala.echo.R.id.playPauseButton
import com.internshala.echo.Songs
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit
import com.internshala.echo.R.id.seekBar
import com.internshala.echo.databases.EchoDatabase


/**
 * A simple [Fragment] subclass.
 */

class SongPlayingFragment : Fragment() {



    object Statified
    {
        var shakeflag:Int=0
        var MY_PREFS_NAME = "ShakeFeature"
        var mSensorManager:SensorManager?=null
        var mSensorListener:SensorEventListener?=null
        var myActivity: Activity? = null
        var favoriteContent:EchoDatabase?=null
        var mediaPlayer: MediaPlayer? = null
        var fab:ImageButton?=null
       var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var sp: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisulization:AudioVisualization?=null
        var glView:GLAudioVisualizationView?=null
    var currentSongHelper: CurrentSongHelper? = null

        var updateSongTime = object : Runnable {
            override fun run() {

                seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromuser: Boolean) {
                        if (fromuser) {
                            mediaPlayer?.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        // TODO Auto-generated method stub
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        // TODO Auto-generated method stub

                    }
                })
                var getCurrent = mediaPlayer?.currentPosition
                val totalDuration = mediaPlayer?.getDuration()
                val currentDuration = mediaPlayer?.getCurrentPosition()

                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long)%60




                ))
                var utils = null
                seekBar?.max= totalDuration as Int
                seekBar?.setProgress(currentDuration!!)




               Handler().postDelayed(this, 1000)
            }
        }



        fun Nothing.progressToTimer(progress: Int, tD: Int): Int {
            var td = tD
            var cd= 0
            td= td / 1000
            cd= (progress.toDouble() / 100 * td).toInt()

            // return current duration in milliseconds
            return cd * 1000
        }
    }



    object Staticated
    {
        var MY_PREFS_SHUFFLE="Shuffle feature"
        var MY_PREFS_LOOP="Loop feature"
        fun playNext(check: String) {


             if (check.equals("PlayNextNormal", true)) {

                 Statified.sp = Statified.sp + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {

                var randomObject = Random()
    var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)

                Statified.sp= randomPosition
            }

             if (Statified.sp == Statified.fetchSongs?.size) {
                Statified.sp= 0
            }
            Statified.currentSongHelper?.isLoop = false

             var nextSong = Statified.fetchSongs?.get(Statified.sp)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

             updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

             Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()

                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_on))
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_off))
            }
        }

        fun playPrevious() {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            Statified.sp = Statified.sp - 1

            if (Statified.sp == -1) {
                Statified.sp = 0
            }
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isLoop = false

            var nextSong = Statified.fetchSongs?.get(Statified.sp)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()

                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_on))
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_off))
            }
        }

       fun updateTextViews(songTitle: String, songArtist: String) {
           var updatedSongTitle=songTitle
           var updatedSongArtist=songArtist
           if(updatedSongArtist.equals("<unknown>",true))
           {
               updatedSongArtist="unknown"
           }
           if(updatedSongTitle.equals("<unknown>",true))
           {
               updatedSongTitle="unknown"
           }
            Statified.songTitleView?.setText(updatedSongTitle)
            Statified.songArtistView?.setText(updatedSongArtist)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {

            val finalTime = mediaPlayer.duration

            val startTime = mediaPlayer.currentPosition

            Statified.startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )

             Statified.endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )

         Handler().postDelayed(Statified.updateSongTime, 1000)
        }

        fun onSongComplete() {

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {

                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.sp)
                    Statified.currentSongHelper?.sp = Statified.sp
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_on))
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_off))
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)

        /*Linking views with their ids*/
        setHasOptionsMenu(true)
        activity?.title="Now Playing"
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView=view?.findViewById(R.id.visualizer_view)
        Statified.fab=view?.findViewById(R.id.favoriteIcon)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisulization=Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisulization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)
        mAcceleration=0.0f
        mAccelerationCurrent=SensorManager.GRAVITY_EARTH
        mAccelerationLast=SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onPause() {
        Statified.audioVisulization?.onPause()
        super.onPause()

        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        Statified.audioVisulization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Statified.mSensorManager=Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val item:MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_redirect->{
                Statified.myActivity?.onBackPressed()
                return false

            }
        }
        return false
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favoriteContent= EchoDatabase(Statified.myActivity)
        /*Initialising the params of the current song helper object*/
        Statified.currentSongHelper = CurrentSongHelper()

        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()

            /*Here we fetch the received bundle data for current position and the list of all songs*/
            Statified.sp = arguments?.getInt("songPosition")!!
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")


            /*Now store the song details to the current song helper object so that they can be used later*/
            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.sp =Statified.sp
            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        } catch (e: Exception) {
            e.printStackTrace()
        }
var fromFavBottomBar= arguments!!.getString("FavBottomBar") as? String
var fromMainBottomBar= arguments!!.getString("MainBottomBar") as? String
        if(fromFavBottomBar != null)
        {
            Statified.mediaPlayer=FavoriteFragment.Statified.mediaPlayer
        }
        else if(fromMainBottomBar!=null)
        {
            Statified.mediaPlayer=MainScreenFragment.Statified.mediaPlayer
        }
        else {
            if(Statified.mediaPlayer !=null)
            {
                if(Statified.mediaPlayer?.isPlaying as Boolean)
                {
                    Statified.mediaPlayer?.pause()
                }}
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()
        }
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        /*Handling the event when media player finishes a song*/
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

/*Making the click actions function*/
        clickHandler()
        var visualizationHandeler=DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context,0)
        Statified. audioVisulization?.linkTo(visualizationHandeler)



        if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_on))
        }
        else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_off))
        }


        var shufflePrefs=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var loopPrefs=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isShuffleON=shufflePrefs?.getBoolean("feature",false)
        var isLoopON=loopPrefs?.getBoolean("feature",false)
        if(isShuffleON as Boolean)
        {
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
        }
        else
        {
            Statified.currentSongHelper?.isShuffle=false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }


        if(isLoopON as Boolean)
        {
            Statified.currentSongHelper?.isShuffle=false
            Statified.currentSongHelper?.isLoop=true
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        else
        {
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

    }


   fun clickHandler() {


    Statified.fab?.setOnClickListener({


        if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_off))
            Statified.favoriteContent?.deleteFavorite(Statified.currentSongHelper?.songId?.toInt() as Int)
            Toast.makeText(Statified.myActivity,"Removed From Favorite",Toast.LENGTH_SHORT).show()
        }
        else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!,R.drawable.favorite_on))
            Statified.favoriteContent?.storeAsFavorite(Statified.currentSongHelper?.songId?.toInt() as Int,Statified.currentSongHelper?.songArtist,Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath)
        Toast.makeText(Statified.myActivity,"Added To Favorite",Toast.LENGTH_SHORT).show()
        }
    })
        /*The implementation will be taught in the coming topics*/
    Statified.shuffleImageButton?.setOnClickListener({
var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
            if(Statified.currentSongHelper?.isShuffle as Boolean)
            {
                Statified.currentSongHelper?.isShuffle=false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("festure",false)
                editorShuffle?.apply()


            }
            else
            {
                Statified.currentSongHelper?.isShuffle=true;
                Statified.currentSongHelper?.isLoop=false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }
        })

      Statified.nextImageButton?.setOnClickListener({

        Statified.currentSongHelper?.isPlaying = true
        Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        var prefs=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
        prefs?.putBoolean("feature",false)
        Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        Statified.currentSongHelper?.isLoop=false

            if (Statified.currentSongHelper?.isShuffle as Boolean) {

                 Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {

               Staticated.playNext("PlayNextNormal")
            }
        })

       Statified.previousImageButton?.setOnClickListener({
        Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        var prefs=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
        prefs?.putBoolean("feature",false)
        Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        Statified.currentSongHelper?.isLoop=false
  Statified.currentSongHelper?.isPlaying = true

            if (Statified.currentSongHelper?.isLoop as Boolean) {

                 Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

            }

              Staticated.playPrevious()
        })

           Statified.loopImageButton?.setOnClickListener({
        var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
        var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
             if (Statified.currentSongHelper?.isLoop as Boolean) {

                Statified.currentSongHelper?.isLoop = false

                 Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()

            } else {
editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                 Statified.currentSongHelper?.isLoop = true

                Statified.currentSongHelper?.isShuffle = false

                 Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

                  Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }
        })

          Statified.playPauseImageButton?.setOnClickListener({

             if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

              } else {
                Statified.mediaPlayer?.start()


                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

      var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    fun bindShakeListener() {

       Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
            override fun onSensorChanged(event: SensorEvent) {

                 val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
   mAccelerationLast = mAccelerationCurrent

                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()

                val delta = mAccelerationCurrent - mAccelerationLast

                  mAcceleration = mAcceleration * 0.9f + delta

                 if (mAcceleration > 12) {

                     var prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                     var isAllowed = prefs?.getBoolean("feature", false)
                     if (isAllowed as Boolean) {
                         Staticated.playNext("PlayNextNormal")
                     }
                }
            }
        }
    }
}


