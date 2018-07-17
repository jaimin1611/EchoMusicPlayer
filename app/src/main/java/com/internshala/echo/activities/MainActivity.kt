package com.internshala.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.internshala.echo.R
import com.internshala.echo.adapters.NavigationDrawerAdapter
import com.internshala.echo.fragments.FavoriteFragment
import com.internshala.echo.fragments.MainScreenFragment
import com.internshala.echo.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {

object statified {
    var drawerLayout: DrawerLayout? = null
    var notificationManager:NotificationManager?=null
}
    var trackNotificationBuilder:Notification?=null
var navigationDrawerIconsList:ArrayList<String> = arrayListOf()
    var images_for_navdrawer= intArrayOf(R.drawable.sng,R.drawable.fav,R.drawable.navigation_settings,R.drawable.abt)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.statified.drawerLayout=findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorite")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")
        val toggle=ActionBarDrawerToggle(this@MainActivity,MainActivity.statified.drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()



        val mainScreenFragment= MainScreenFragment()

       this.supportFragmentManager.beginTransaction().add(R.id.details_fragment,mainScreenFragment,"MainScreenFragment").commit()
var _navigationAdapter=NavigationDrawerAdapter(navigationDrawerIconsList,images_for_navdrawer,this)
        _navigationAdapter.notifyDataSetChanged()
        var navigation_recycler_view=findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager=LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator=DefaultItemAnimator()
        navigation_recycler_view.adapter=_navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)


        val intent=Intent(this@MainActivity,MainActivity::class.java)
        val pIntent=PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis().toInt(),intent,0)
        trackNotificationBuilder=Notification.Builder(this)
                .setContentTitle("A track is playing in background").setSmallIcon(R.drawable.echo_icon)
                .setContentIntent(pIntent).setOngoing(true).setAutoCancel(true).build()

        statified.notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onStart() {
        super.onStart()

        try{
            statified.notificationManager?.cancel(1999)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()


        try{
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                statified.notificationManager?.notify(1999,trackNotificationBuilder)
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        try{
            statified.notificationManager?.cancel(1999)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}