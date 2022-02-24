package carrira.elan.tamagotchi

import android.content.Context
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

class JSONHelper {
    private val fileName = "inventory.json"

    fun getMeal(context : Context) : ArrayList<Meal>{
        val array : ArrayList<Meal> = arrayListOf()
        val jsonObject = JSONTokener(getJSONString(context)).nextValue() as JSONObject
        val jsonArray = jsonObject.getJSONObject("meal")

        array.add(Meal("apple", jsonArray.getInt("apple")))
        array.add(Meal("banana", jsonArray.getInt("banana")))
        array.add(Meal("soul", jsonArray.getInt("soul")))

        return array
    }

    fun updateMeal(){
        //TODO set meal num to json
    }

    fun getNeeds(context: Context): Needs {
        val jsonObject = JSONTokener(getJSONString(context)).nextValue() as JSONObject
        val jsonArray = jsonObject.getJSONObject("needs")

        return Needs(
            jsonArray.getInt("happy"),
            jsonArray.getInt("hungry"),
            jsonArray.getInt("sleep"))
    }

    fun setNeeds(needs : Needs, context: Context){
        val jsonObject = JSONTokener(getJSONString(context)).nextValue() as JSONObject
        val jsonArray = jsonObject.getJSONObject("needs")

        //jsonArray.put("happy", needs.getHappy())
        //jsonArray.put("hungry", needs.getHungry())
        //jsonArray.put("sleep", needs.getSleep())

    }

    /**
     * Get JSON file like String
     */
    private fun getJSONString(context: Context) : String?{
        val jsonString : String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        }catch(e : IOException){
            e.printStackTrace()
            return null
        }
        return jsonString
    }
}

class Meal(val title : String, val num : Int)

class Needs(
    private var happy : Int,
    private var hungry : Int,
    private var sleep: Int){

    fun getHappy():Int{return happy}
    fun getHungry():Int{return hungry}
    fun getSleep():Int{return sleep}

    fun setHappy(happy: Int){
        this.happy = happy
    }
    fun setHungry(hungry: Int){
        this.hungry = hungry
    }
    fun setSleep(sleep: Int){
        this.sleep = sleep
    }
}