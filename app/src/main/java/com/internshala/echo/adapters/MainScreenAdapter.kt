package com.internshala.echo.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.fragments.MainScreenFragment
import com.internshala.echo.fragments.SettingsFragment
import com.internshala.echo.fragments.SongPlayingFragment

class MainScreenAdapter(_songDetails:ArrayList<Songs>,_context:Context):RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>()
{


    var songDetails:ArrayList<Songs>?=null
    var mContext:Context?=null
    init{
        this.songDetails=_songDetails
        this.mContext=_context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
 var itemView:View?=null

        if(MainScreenFragment.Statified.flag==1) {
            MainScreenFragment.Statified.tlbar?.setBackgroundColor(Color.WHITE)
             itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_custom_mainscreen_adapter_black, parent, false)
        }else{
            MainScreenFragment.Statified.tlbar?.setBackgroundColor(Color.rgb(0,3,58))
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        }




           return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        if(songDetails == null)
        {
            return 0
        }
        else
        {
            return (songDetails as ArrayList<Songs>).size
        }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject=songDetails?.get(position)
        holder.trackTitle?.text=songObject?.songTitle
        holder.trackArtist?.text=songObject?.artist
        holder.contentHolder?.setOnClickListener({
            val songPlayingFragment= SongPlayingFragment()
            var args = Bundle()

            args.putString("songArtist", songObject?.artist)
            args.putString("songTitle", songObject?.songTitle)
            args.putString("path", songObject?.songData)
            args.putInt("songId", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", position)
            songPlayingFragment.arguments=args

              args.putParcelableArrayList("songData", songDetails)
            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()

        })

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var trackTitle:TextView?=null
        var trackArtist:TextView?=null
        var contentHolder:RelativeLayout?=null
        init{
            trackTitle=view.findViewById(R.id.trackTitle) as TextView
            trackArtist=view.findViewById(R.id.trackArtist) as TextView
            contentHolder=view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }

}