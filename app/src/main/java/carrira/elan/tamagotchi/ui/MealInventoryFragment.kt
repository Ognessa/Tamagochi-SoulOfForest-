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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.meal_layout, container, false)

        ivGetLeftMeal = view.findViewById(R.id.get_left_meal)
        ivGetRightMeal = view.findViewById(R.id.get_right_meal)
        ivMeal = view.findViewById(R.id.iv_meal)
        tvCount = view.findViewById(R.id.tv_meal_count)

        val mealList : ArrayList<Meal>? = context?.let { JSONHelper().getMeal(it) }
        var currentId = 0
        var currentMeal : Meal = mealList!!.get(currentId)
        setNewMeal(currentMeal)

        //get previous meal
        ivGetLeftMeal.setOnClickListener {
            if(currentId > 0){
                currentId--
                currentMeal = mealList.get(currentId)

                setNewMeal(currentMeal)
            }
        }

        ivMeal.setOnClickListener {
            if(currentMeal.num > 0){
                val needs = context?.let { it1 -> JSONHelper().getNeeds(it1) }

                if(needs!!.getHungry() + 25 < 100)
                    needs!!.setHungry(needs.getHungry() + 25)
                else needs!!.setHungry(100)

                context?.let { it1 -> JSONHelper().setNeeds(needs, it1) }

                //TODO minus meal number after add shop
                //currentMeal.num = currentMeal.num - 1
                //mealList[currentId] = currentMeal
                //context?.let { it1 -> JSONHelper().setMeal(mealList, it1) }
                setNewMeal(currentMeal)

                Toast.makeText(context, "Ням ням ням", Toast.LENGTH_LONG).show()

                val intent = Intent("carrira.elan.tamagotchi.UPDATE_INFO_ACTION")
                context?.let{it1 -> it1.sendBroadcast(intent)}
            }else{
                Toast.makeText(context, "Ooops...", Toast.LENGTH_LONG).show()
            }
        }

        //get next meal
        ivGetRightMeal.setOnClickListener {
            if(currentId < mealList.size - 1 ){
                currentId++
                currentMeal = mealList.get(currentId)

                setNewMeal(currentMeal)
            }
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
                meal.title, "drawable", requireContext().packageName)))
        tvCount.text = meal.num.toString()
    }

}
