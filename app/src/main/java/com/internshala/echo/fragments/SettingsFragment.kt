package com.internshala.echo.fragments


import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.*
import com.internshala.echo.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment() {

    var myActivity:Activity?=null
    var shakeSwitch:Switch?=null
    var textT:TextView?=null
    var textA:TextView?=null
    var relL:RelativeLayout?=null
var tlbar:Toolbar?=null
    var darkSwitch:Switch?=null


   object Statified{

       var flag:Int=0

           var MY_PREFS_NAME = "ShakeFeature"

       var MY_PREFS_THEME="darkTheme"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.title="Settings"
        setHasOptionsMenu(true)
        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        shakeSwitch=view?.findViewById(R.id.switchShake)
        darkSwitch=view?.findViewById(R.id.switchTheme)
        textA=view?.findViewById(R.id.darkTheme)
        textT=view?.findViewById(R.id.shaketochange)
        relL=view?.findViewById(R.id.relLayoutSetting)
tlbar=view?.findViewById(R.id.toolbar)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)


        val prefs=myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
        val isAllowed=prefs?.getBoolean("feature",false)
       val pre=myActivity?.getSharedPreferences(Statified.MY_PREFS_THEME,Context.MODE_PRIVATE)
        val isDark=pre?.getBoolean("feature",false)
        if(isDark as Boolean)
        {

            textA?.setTextColor(Color.WHITE)
            textT?.setTextColor(Color.WHITE)
            relL?.setBackgroundColor(Color.BLACK)
tlbar?.setBackgroundColor(Color.WHITE)
            darkSwitch?.isChecked=true
            Statified.flag=1
        }
        else
        {
            textA?.setTextColor(Color.BLACK)
            textT?.setTextColor(Color.BLACK)
            relL?.setBackgroundColor(Color.WHITE)
            tlbar?.setBackgroundColor(Color.rgb(0,3,58))
            darkSwitch?.isChecked=false
            Statified.flag=0
        }
        if(isAllowed as Boolean)
        {
            shakeSwitch?.isChecked=true
        }
        else
        {
            shakeSwitch?.isChecked=false
        }

        shakeSwitch?.setOnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                var editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                var editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }
        })


        darkSwitch?.setOnCheckedChangeListener({compoundButton,b->

            if(b)
            {

                val editor=myActivity?.getSharedPreferences(Statified.MY_PREFS_THEME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
                Statified.flag=1
                 MainScreenFragment.Statified.flag=1
                 FavoriteFragment.Statified.flag=1
                textA?.setTextColor(Color.WHITE)
                textT?.setTextColor(Color.WHITE)
                relL?.setBackgroundColor(Color.BLACK)
            }
            else
            {
                textA?.setTextColor(Color.BLACK)
                textT?.setTextColor(Color.BLACK)
                relL?.setBackgroundColor(Color.WHITE)
                val editor=myActivity?.getSharedPreferences(Statified.MY_PREFS_THEME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.apply()
                Statified.flag=0
                MainScreenFragment.Statified.flag=0
                  FavoriteFragment.Statified.flag=0
            }

        })


    }



    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false

    }
}
