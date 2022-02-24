package carrira.elan.tamagotchi.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import carrira.elan.tamagotchi.JSONHelper
import carrira.elan.tamagotchi.Meal
import carrira.elan.tamagotchi.Needs
import carrira.elan.tamagotchi.R

class MealInventoryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.meal_layout, container, false)

        val ivGetLeftMeal : ImageView = view.findViewById(R.id.get_left_meal)
        val ivGetRightMeal : ImageView = view.findViewById(R.id.get_right_meal)
        val ivMeal : ImageView = view.findViewById(R.id.iv_meal)

        //TODO set meal list from json
        val mealList : ArrayList<Meal>? = context?.let { JSONHelper().getMeal(it) }
        //get previous meal
        ivGetLeftMeal.setOnClickListener {
            val tempNum = context?.let { JSONHelper().getNeeds(it) } as Needs
            var str = "Happy: " + tempNum.getHappy() +
                    "\nHungry: " + tempNum.getHungry() +
                    "\nSleep: " + tempNum.getSleep()
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }

        ivMeal.setOnClickListener {
            var str = ""
            if (mealList != null) {
                for(i in mealList){
                    str += i.title + " - " + i.num + "\n"
                }
            }
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()

        }

        //get next meal
        ivGetRightMeal.setOnClickListener {  }

        return view
    }

    companion object {
        fun newInstance() =
            MealInventoryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}