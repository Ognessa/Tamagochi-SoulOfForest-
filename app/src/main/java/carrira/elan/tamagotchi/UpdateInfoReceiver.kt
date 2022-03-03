package carrira.elan.tamagotchi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.TextView

class UpdateInfoReceiver(val tv_text : TextView) : BroadcastReceiver() {

    val UPDATE_INFO_ACTION = "carrira.elan.tamagotchi.UPDATE_INFO_ACTION"

    @SuppressLint("SetTextI18n")
    override fun onReceive(context: Context, intent: Intent) {
        val needs = JSONHelper().getNeeds(context)
        tv_text.text = "Happy: "+ needs.getHappy() +
                "%\nHungry: " + needs.getHungry() +
                "%\nSleep: " + needs.getSleep() + "%"
    }
}