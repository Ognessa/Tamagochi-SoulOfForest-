package carrira.elan.tamagotchi


import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import carrira.elan.tamagotchi.rooms.BedroomFragment
import carrira.elan.tamagotchi.rooms.DiningRoomFragment
import carrira.elan.tamagotchi.rooms.PlayRoomFragment
import carrira.elan.tamagotchi.ui.MealInventoryFragment
import com.airbnb.lottie.LottieAnimationView
import java.io.File

class MainActivity : AppCompatActivity() {

    //var flag : Boolean = false
    private lateinit var tvTamName : TextView
    //TODO create shadow when night

    private lateinit var ivBedroom : ImageView
    private lateinit var ivDiningRoom : ImageView
    private lateinit var ivPlayRoom : ImageView

    private lateinit var tvNeeds : TextView

    lateinit var mUpdateReceiver : UpdateInfoReceiver
    lateinit var intentFilter : IntentFilter
    private lateinit var lavTamagotchi : LottieAnimationView
    lateinit var userInterface : View


    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeRoom(PlayRoomFragment.newInstance())

        if(!File(applicationContext.filesDir, JSONHelper().pathFile).exists())
            JSONHelper().createJSONDataFile(this)

        tvTamName = findViewById(R.id.tv_tam_name)
        lavTamagotchi = findViewById(R.id.lav_tamagotchi)
        userInterface = findViewById(R.id.lu_interface)

        ivBedroom = findViewById(R.id.iv_bedroom)
        ivDiningRoom = findViewById(R.id.iv_dining_room)
        ivPlayRoom = findViewById(R.id.iv_play_room)

        val needs = JSONHelper().getNeeds(this)
        tvNeeds = findViewById(R.id.tv_needs)
        tvNeeds.text =  "Happy: "+ needs.getHappy() +
                "%\nHungry: " + needs.getHungry() +
                "%\nSleep: " + needs.getSleep() + "%"

        lavTamagotchi.setOnClickListener {
            val n = JSONHelper().getNeeds(this)

            if(n.getHappy()+10 > 100)n.setHappy(100)
            else n.setHappy(n.getHappy()+10)

            JSONHelper().setNeeds(n, this)
            lavTamagotchi.playAnimation()

            val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
            sendBroadcast(intent)
        }

        ivBedroom.setOnClickListener {
            changeRoom(BedroomFragment.newInstance())
            removeMealMenu()
        }

        ivDiningRoom.setOnClickListener {
            changeRoom(DiningRoomFragment.newInstance())
            //ivNightShadow.visibility = View.INVISIBLE
            JSONHelper().setSleepCoeff(-1, this)
            userInterface.background = null
            setMealMenu()
        }

        ivPlayRoom.setOnClickListener {
            changeRoom(PlayRoomFragment.newInstance())
            //ivNightShadow.visibility = View.INVISIBLE
            JSONHelper().setSleepCoeff(-1, this)
            userInterface.background = null
            removeMealMenu()
        }

        val intent = Intent(this, TamagotchiNeedsService::class.java)
        startService(intent)

        mUpdateReceiver = UpdateInfoReceiver(tvNeeds)
        intentFilter = IntentFilter(mUpdateReceiver.UPDATE_INFO_ACTION)

    }

    override fun onResume() {
        super.onResume()
        JSONHelper().setSleepCoeff(-1, this)
        userInterface.background = null
        registerReceiver(mUpdateReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mUpdateReceiver)
    }

    private fun changeRoom(fr : Fragment){
        val fragmentManager : FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fl_room, fr)
            .commit()
    }

    private fun setMealMenu(){
        val fragmentManager : FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.meal_frame, MealInventoryFragment.newInstance())
            .commit()
    }

    private fun removeMealMenu(){
        val fragmentManager : FragmentManager = supportFragmentManager
                if(fragmentManager.findFragmentById(R.id.meal_frame) != null){
                    fragmentManager.beginTransaction()
                        .remove(fragmentManager.findFragmentById(R.id.meal_frame) as MealInventoryFragment)
                        .commit()
                }
    }
}