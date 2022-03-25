package carrira.elan.tamagotchi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import carrira.elan.tamagotchi.JSONHelper
import carrira.elan.tamagotchi.Meal
import carrira.elan.tamagotchi.R

class MealInventoryFragment : Fragment() {

    private lateinit var ivGetLeftMeal : ImageView
    private lateinit var ivGetRightMeal : ImageView
    private lateinit var ivMeal : ImageView
    private lateinit var tvCount : TextView

    private val jsonHelper = JSONHelper()
    private lateinit var mealList : ArrayList<Meal>
    private var currentId : Int = 0
    private lateinit var currentMeal : Meal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.meal_layout, container, false)

        ivGetLeftMeal = view.findViewById(R.id.get_left_meal)
        ivGetRightMeal = view.findViewById(R.id.get_right_meal)
        ivMeal = view.findViewById(R.id.iv_meal)
        tvCount = view.findViewById(R.id.tv_meal_count)

        mealList = jsonHelper.getMeal(requireContext())
        currentId = 0
        currentMeal = mealList[currentId]
        setNewMeal(currentMeal)

        //get previous meal
        ivGetLeftMeal.setOnClickListener {
            getPrevMeal()
        }

        //eat current meal
        ivMeal.setOnClickListener {
            if(currentMeal.num > 0){
                eat()
            }else{
                Toast.makeText(context, "Ooops...", Toast.LENGTH_LONG).show()
            }
        }

        //get next meal
        ivGetRightMeal.setOnClickListener {
            getNextMeal()
        }

        return view
    }

    companion object {
        fun newInstance() =
            MealInventoryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setNewMeal(meal:Meal){
        ivMeal.setImageDrawable(requireContext().resources.getDrawable(
            requireContext().resources.getIdentifier(
                meal.title, "drawable", requireContext().packageName), null))
        tvCount.text = meal.num.toString()
    }

    private fun eat(){
        val needs = jsonHelper.getNeeds(requireContext())

        if(needs.getHungry() + currentMeal.satiety < 100)
            needs.setHungry(needs.getHungry() + currentMeal.satiety)
        else needs.setHungry(100)

        jsonHelper.setNeeds(needs, requireContext())

        currentMeal.num = currentMeal.num - 1
        mealList[currentId] = currentMeal
        jsonHelper.setMeal(mealList, requireContext())
        setNewMeal(currentMeal)

        Toast.makeText(context, "Ням ням ням", Toast.LENGTH_LONG).show()

        val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
        context?.sendBroadcast(intent)

        //TODO delete meal when num == 0 and move meal to prev/next
    }

    private fun getNextMeal(){
        if(currentId < mealList.size - 1 ){
            currentId++
            currentMeal = mealList[currentId]

            setNewMeal(currentMeal)
        }
    }

    private fun getPrevMeal(){
        if(currentId > 0){
            currentId--
            currentMeal = mealList[currentId]

            setNewMeal(currentMeal)
        }
    }
}
