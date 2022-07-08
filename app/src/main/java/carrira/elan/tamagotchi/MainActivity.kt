package carrira.elan.tamagotchi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import carrira.elan.tamagotchi.other.ShopActivity
import carrira.elan.tamagotchi.rooms.BedroomFragment
import carrira.elan.tamagotchi.rooms.DiningRoomFragment
import carrira.elan.tamagotchi.rooms.PlayRoomFragment
import carrira.elan.tamagotchi.ui.MealInventoryFragment
import com.airbnb.lottie.LottieAnimationView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var tvTamName : TextView

    private lateinit var ivBedroom : ImageView
    private lateinit var ivDiningRoom : ImageView
    private lateinit var ivPlayRoom : ImageView
    private lateinit var ivShop : ImageView

    private lateinit var ivHappyIndicator : ImageView
    private lateinit var ivHungryIndicator : ImageView
    private lateinit var ivSleepyIndicator : ImageView

    private lateinit var tvNeeds : TextView

    private lateinit var lavTamagotchi : View
    private lateinit var userInterface : View

    private val jsonHelper = JSONHelper()
    private var blinking : Int = 0

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeRoom(PlayRoomFragment.newInstance())

        /**
         * Update or create file
         */
        if(!File(applicationContext.filesDir, jsonHelper.pathFile).exists()
            || jsonHelper.isJSONChanged(this)) jsonHelper.createJSONDataFile(this)

        tvTamName = findViewById(R.id.tv_tam_name)
        lavTamagotchi = findViewById(R.id.lav_tamagotchi)
        userInterface = findViewById(R.id.lu_interface)

        ivBedroom = findViewById(R.id.iv_bedroom)
        ivDiningRoom = findViewById(R.id.iv_dining_room)
        ivPlayRoom = findViewById(R.id.iv_play_room)
        ivShop = findViewById(R.id.iv_shop)

        ivHappyIndicator = findViewById(R.id.iv_happy_indicator)
        ivHungryIndicator = findViewById(R.id.iv_hungry_indicator)
        ivSleepyIndicator = findViewById(R.id.iv_sleepy_indicator)

        tvNeeds = findViewById(R.id.tv_needs)

        lavTamagotchi.setOnClickListener {
            val n = jsonHelper.getNeeds(this)

            if(n.getHappy()+10 > 100) n.setHappy(100)
            else n.setHappy(n.getHappy()+10)

            jsonHelper.setNeeds(n, this)
            findViewById<LottieAnimationView>(R.id.lav_ears).playAnimation()
            updateNeeds()
        }

        /**
         * Set or change name for pet
         */
        if(!jsonHelper.isTamagotchiNamed(this)){setNameToTamagotchi()}
        tvTamName.text = jsonHelper.getTamagotchiName(this)
        tvTamName.setOnClickListener { setNameToTamagotchi() }

        ivBedroom.setOnClickListener {
            changeRoom(BedroomFragment.newInstance())
            removeMealMenu()
        }

        ivDiningRoom.setOnClickListener {
            changeRoom(DiningRoomFragment.newInstance())
            jsonHelper.setSleepCoeff(-1, this)
            userInterface.background = null
            setMealMenu()
        }

        ivPlayRoom.setOnClickListener {
            changeRoom(PlayRoomFragment.newInstance())
            jsonHelper.setSleepCoeff(-1, this)
            userInterface.background = null
            removeMealMenu()
        }

        ivShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        /**
         * Play tamagotchi eyes animation
         * Update needs text
         * Every 4sec
         * */
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run(){
                if(blinking == 60){
                    findViewById<LottieAnimationView>(R.id.lav_eyes).playAnimation()
                    blinking = 0
                }
                updateNeeds()
                blinking++
                mainHandler.postDelayed(this, 100)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TamagotchiNeedsService::class.java)
        startService(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        jsonHelper.setSleepCoeff(-1, this)
        userInterface.background = null
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

    fun updateNeeds(){
        val needs = jsonHelper.getNeeds(applicationContext)
        val onePercentSize = (resources.getDimensionPixelOffset(R.dimen.ui_icon_size) / 100.0)
        tvNeeds.text = needs.toString()
        ivHappyIndicator.layoutParams.height = (needs.getHappy() * onePercentSize).toInt()
        ivHungryIndicator.layoutParams.height  = (needs.getHungry() * onePercentSize).toInt()
        ivSleepyIndicator.layoutParams.height = (needs.getSleep() * onePercentSize).toInt()
        ivHappyIndicator.requestLayout()
        ivHungryIndicator.requestLayout()
        ivSleepyIndicator.requestLayout()
    }

    @SuppressLint("InflateParams")
    fun setNameToTamagotchi(){
        val renamePetView : View = LayoutInflater.from(this).inflate(R.layout.pet_name_window, null)
        val enterName = renamePetView.findViewById<EditText>(R.id.et_rename_pet)
        val positivBtn = renamePetView.findViewById<TextView>(R.id.tv_rename_ok)
        val negativeBtn = renamePetView.findViewById<TextView>(R.id.tv_rename_cancel)

        val builder = AlertDialog.Builder(this)
        builder.setView(renamePetView)

        val alert : AlertDialog = builder.create()

        positivBtn.setOnClickListener {
            jsonHelper.setTamagotchiName(this, enterName.text.toString())
            tvTamName.text = jsonHelper.getTamagotchiName(this)
            alert.cancel()
        }
        negativeBtn.setOnClickListener {
            tvTamName.text = jsonHelper.getTamagotchiName(this)
            alert.cancel()
        }

        alert.show()
    }
}