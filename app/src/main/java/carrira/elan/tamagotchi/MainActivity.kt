package carrira.elan.tamagotchi

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
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

    lateinit var tvNeeds : TextView

    private lateinit var mUpdateReceiver : UpdateInfoReceiver
    private lateinit var intentFilter : IntentFilter
    private lateinit var lavTamagotchi : View
    private lateinit var userInterface : View

    private val jsonHelper = JSONHelper()

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

        tvNeeds = findViewById(R.id.tv_needs)

        lavTamagotchi.setOnClickListener {
            val n = jsonHelper.getNeeds(this)

            if(n.getHappy()+10 > 100) n.setHappy(100)
            else n.setHappy(n.getHappy()+10)

            jsonHelper.setNeeds(n, this)
            findViewById<LottieAnimationView>(R.id.lav_ears).playAnimation()

            val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
            sendBroadcast(intent)
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
            //ivNightShadow.visibility = View.INVISIBLE
            jsonHelper.setSleepCoeff(-1, this)
            userInterface.background = null
            setMealMenu()
        }

        ivPlayRoom.setOnClickListener {
            changeRoom(PlayRoomFragment.newInstance())
            //ivNightShadow.visibility = View.INVISIBLE
            jsonHelper.setSleepCoeff(-1, this)
            userInterface.background = null
            removeMealMenu()
        }

        ivShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        val intent = Intent(this, TamagotchiNeedsService::class.java)
        startService(intent)
        /*val connection = object : ServiceConnection{
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                TODO("Not yet implemented")
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                TODO("Not yet implemented")
            }

        }
        bindService(intent, connection, BIND_AUTO_CREATE)*/

        mUpdateReceiver = UpdateInfoReceiver(tvNeeds)
        intentFilter = IntentFilter(mUpdateReceiver.UPDATE_INFO_ACTION)

        /**
         * Play tamagotchi eyes animation every 4sec
         * */
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run(){
                findViewById<LottieAnimationView>(R.id.lav_eyes).playAnimation()
                mainHandler.postDelayed(this, 4000)
            }
        })

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        jsonHelper.setSleepCoeff(-1, this)
        userInterface.background = null
        val needs = jsonHelper.getNeeds(this)
        tvNeeds.text =  needs.toString()
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