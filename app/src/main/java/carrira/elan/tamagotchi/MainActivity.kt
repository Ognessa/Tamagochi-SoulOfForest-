package carrira.elan.tamagotchi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import carrira.elan.tamagotchi.rooms.BedroomFragment
import carrira.elan.tamagotchi.rooms.DiningRoomFragment
import carrira.elan.tamagotchi.rooms.PlayRoomFragment
import carrira.elan.tamagotchi.ui.MealInventoryFragment
import com.airbnb.lottie.LottieAnimationView

class MainActivity : AppCompatActivity() {

    //var flag : Boolean = false
    private lateinit var tvTamName : TextView

    private lateinit var ivBedroom : ImageView
    private lateinit var ivDiningRoom : ImageView
    private lateinit var ivPlayRoom : ImageView

    private lateinit var tvHappy : TextView
    private lateinit var tvHungry : TextView
    private lateinit var tvSleep : TextView

    private lateinit var lavTamagotchi : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeRoom(PlayRoomFragment.newInstance())

        tvTamName = findViewById(R.id.tv_tam_name)
        lavTamagotchi = findViewById(R.id.lav_tamagotchi)

        ivBedroom = findViewById(R.id.iv_bedroom)
        ivDiningRoom = findViewById(R.id.iv_dining_room)
        ivPlayRoom = findViewById(R.id.iv_play_room)



        lavTamagotchi.setOnClickListener {
            lavTamagotchi.playAnimation()
        }

        ivBedroom.setOnClickListener {
            changeRoom(BedroomFragment.newInstance())
            removeMealMenu()
        }

        ivDiningRoom.setOnClickListener {
            changeRoom(DiningRoomFragment.newInstance())
            setMealMenu()
        }

        ivPlayRoom.setOnClickListener {
            changeRoom(PlayRoomFragment.newInstance())
            removeMealMenu()
        }

        val intent = Intent(this, TamagotchiNeedsService::class.java)
        startService(intent)
    }

    override fun onPause() {
        super.onPause()
        stopService(intent)
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

    /*fun onTamagotchiClickListener(){
        if(!flag){
            tvTamName.text = "Forest spirit"
            flag = true
        }
        else{
            tvTamName.text = "Tamagotchi"
            flag = false
        }
    }*/
}