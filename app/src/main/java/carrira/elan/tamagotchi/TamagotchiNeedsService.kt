package carrira.elan.tamagotchi

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TamagotchiNeedsService : Service() {

    val TAG = "DEBUG"
    lateinit var mScheduledExecutorService : ScheduledExecutorService

    override fun onBind(intent: Intent): IBinder {
        val bind : IBinder? = null
        return bind!!
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        mScheduledExecutorService = Executors.newScheduledThreadPool(1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        mScheduledExecutorService.scheduleAtFixedRate(
            Runnable {
                val needs = JSONHelper().getNeeds(this)
                if(needs.getHappy() > 0) needs.setHappy(needs.getHappy()-1)
                if(needs.getHungry() > 0) needs.setHungry(needs.getHungry()-1)
                if(needs.getSleep() > 0) needs.setSleep(needs.getSleep()-1)
                Log.d(TAG, "Happy: " + needs.getHappy() +
                                "\nHungry: " + needs.getHungry() +
                                "\nSleep: " + needs.getSleep())
                JSONHelper().setNeeds(needs, this)

                val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
                sendBroadcast(intent)
            }, 5000, 5000, TimeUnit.MILLISECONDS
        )
        return START_STICKY
    }

    override fun onDestroy() {
        mScheduledExecutorService.shutdownNow()
        Log.d(TAG, "onDestroy")
    }
}