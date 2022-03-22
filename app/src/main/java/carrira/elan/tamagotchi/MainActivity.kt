package carrira.elan.tamagotchi


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var tvNeeds : TextView

    lateinit var mUpdateReceiver : UpdateInfoReceiver
    lateinit var intentFilter : IntentFilter
    private lateinit var lavTamagotchi : View
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
        ivShop = findViewById(R.id.iv_shop)

        tvNeeds = findViewById(R.id.tv_needs)

        lavTamagotchi.setOnClickListener {
            val n = JSONHelper().getNeeds(this)

            if(n.getHappy()+10 > 100)n.setHappy(100)
            else n.setHappy(n.getHappy()+10)

            JSONHelper().setNeeds(n, this)
            findViewById<LottieAnimationView>(R.id.lav_ears).playAnimation()
            findViewById<LottieAnimationView>(R.id.lav_eyes).playAnimation()

            val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
            sendBroadcast(intent)
        }

        /**
         * Set or change name for pet
         */
        if(!JSONHelper().isTamagotchiNamed(this)){setNameToTamagotchi()}
        tvTamName.text = JSONHelper().getTamagotchiName(this)
        tvTamName.setOnClickListener { setNameToTamagotchi() }

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

        ivShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        val intent = Intent(this, TamagotchiNeedsService::class.java)
        startService(intent)

        mUpdateReceiver = UpdateInfoReceiver(tvNeeds)
        intentFilter = IntentFilter(mUpdateReceiver.UPDATE_INFO_ACTION)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        JSONHelper().setSleepCoeff(-1, this)
        userInterface.background = null
        val needs = JSONHelper().getNeeds(this)
        tvNeeds.text =  "Happy: "+ needs.getHappy() +
                "%\nHungry: " + needs.getHungry() +
                "%\nSleep: " + needs.getSleep() + "%"
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

    fun setNameToTamagotchi(){
        val enterName = EditText(this)

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Name your pet:")
            .setView(enterName)
            .setPositiveButton("Save", DialogInterface.OnClickListener { dialogInterface, i ->
                JSONHelper().setTamagotchiName(this, enterName.text.toString())
                tvTamName.text = JSONHelper().getTamagotchiName(this)
                dialogInterface.cancel()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                tvTamName.text = JSONHelper().getTamagotchiName(this)
                dialogInterface.cancel()
            })

        val alert : AlertDialog = builder.create()
        alert.show()
    }
}