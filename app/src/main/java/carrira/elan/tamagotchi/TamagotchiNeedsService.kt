package carrira.elan.tamagotchi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TamagotchiNeedsService : Service() {

    val TAG = "DEBUG"
    lateinit var mScheduledExecutorService : ScheduledExecutorService
    lateinit var notMan : NotificationManager
    lateinit var notCompatB : NotificationCompat.Builder
    val channelId = 123

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        mScheduledExecutorService = Executors.newScheduledThreadPool(1)

        notMan = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notCompatB = getNotificationBuilder()
        notCompatB.setContentTitle("Tamagotchi")
            .setSmallIcon(R.drawable.ic_play_room)

    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return NotificationCompat.Builder(this)
        else{
            val channelId = "channel_id"

            if(notMan.getNotificationChannel(channelId) == null) {
                val channel =
                    NotificationChannel(channelId, "Some text", NotificationManager.IMPORTANCE_LOW)
                notMan.createNotificationChannel(channel)
            }

            return NotificationCompat.Builder(this, channelId)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mScheduledExecutorService.scheduleAtFixedRate(Runnable {
            updateNeeds()
        }, 30000, 30000, TimeUnit.MILLISECONDS)

        return START_STICKY
    }

    override fun onDestroy() {
        mScheduledExecutorService.shutdownNow()
        super.onDestroy()
    }

    fun notification(text : String): Notification {
        return notCompatB.setContentText(text).build()
    }

    fun updateNeeds(){
        val sleepCoefficient = JSONHelper().getSleepCoeff(this)
        val needs = JSONHelper().getNeeds(this)

        if(needs.getHappy() > 0) needs.setHappy(needs.getHappy()-1)
        if(needs.getHungry() > 0) needs.setHungry(needs.getHungry()-1)
        if(needs.getSleep() >= 0){
            if(needs.getSleep() + sleepCoefficient > 100)
                needs.setSleep(100)
            else if (needs.getSleep() + sleepCoefficient < 0)
                needs.setSleep(0)
            else
                needs.setSleep(needs.getSleep() + sleepCoefficient)
        }


        Log.d(TAG, "Happy: " + needs.getHappy() +
                "\nHungry: " + needs.getHungry() +
                "\nSleep: " + needs.getSleep())
        JSONHelper().setNeeds(needs, this)

        if(needs.getHungry() <= 10)
            notMan.notify(channelId, notification("I am hungry!"))
        if(needs.getHappy() <= 10)
            notMan.notify(channelId, notification("I want to play with you!"))
        if(needs.getSleep() <= 10)
            notMan.notify(channelId, notification("I am sleepy!"))
    }

}