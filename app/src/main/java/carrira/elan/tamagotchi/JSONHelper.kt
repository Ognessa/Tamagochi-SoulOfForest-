package carrira.elan.tamagotchi

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.Exception

class JSONHelper {
    private val fileName = "inventory.json"
    @RequiresApi(Build.VERSION_CODES.R)
    val pathFile : String = "data.txt"

    /**
     * Get list of meal from local file
     */
    fun getMeal(context : Context) : ArrayList<Meal>{
        val array : ArrayList<Meal> = arrayListOf()
        val jsonObject = readDataFromFile(context)
        val jsonArray = jsonObject.getJSONObject("meal")

        array.add(Meal("apple", jsonArray.getInt("apple")))
        array.add(Meal("banana", jsonArray.getInt("banana")))
        array.add(Meal("soul", jsonArray.getInt("soul")))

        return array
    }

    /**
     * Update information about meal to local file
     */
    fun setMeal(list:ArrayList<Meal>, context: Context){
        val jsonObject = readDataFromFile(context)
        val jsonArray = jsonObject.getJSONObject("meal")

        for(i in list){
            jsonArray.put(i.title, i.num)
        }

        writeDataToFile(jsonObject.toString(), context)
    }

    /**
     * Get data about tamagotchi needs from local file
     */
    fun getNeeds(context: Context): Needs {
        val jsonObject = readDataFromFile(context)
        val jsonArray = jsonObject.getJSONObject("needs")

        return Needs(
            jsonArray.getInt("happy"),
            jsonArray.getInt("hungry"),
            jsonArray.getInt("sleep"))
    }

    /**
     * Update needs in local files
     */
    fun setNeeds(needs : Needs, context: Context){
        val jsonObject = readDataFromFile(context)
        val jsonArray = jsonObject.getJSONObject("needs")

        jsonArray.put("happy", needs.getHappy())
        jsonArray.put("hungry", needs.getHungry())
        jsonArray.put("sleep", needs.getSleep())

        writeDataToFile(jsonObject.toString(), context)
    }

    /**
     * Create a file of data in local storage
     * May be used only one time!!!!!
     */
    fun createJSONDataFile(context: Context){
        writeDataToFile(getJSONString(context).toString(), context)
    }

    /**
     * Get all data from local file
     */
    private fun readDataFromFile(context:Context):JSONObject{
        val path = context.applicationContext.filesDir
        val file = File(path, pathFile)
        return JSONTokener(file.bufferedReader().use { it.readText() }).nextValue() as JSONObject
    }

    /**
     * Write new data to local file
     */
    private fun writeDataToFile(str:String, context: Context){
        val path = context.applicationContext.filesDir
        try{
            val fOut = FileOutputStream(File(path, pathFile))
            fOut.write(str.toByteArray(Charsets.UTF_8))
            fOut.close()
            Log.d("DEBUG", "Success create file!")
        }catch (e:Exception){
            Log.d("DEBUG", "Cannot create file!")
        }
    }

    /**
     * Get JSON file from assets like String
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

/**
 * Object classes
 */

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