package carrira.elan.tamagotchi

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(val list : ArrayList<Meal>, val context : Context) : RecyclerView.Adapter<RVAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivMeal : ImageView = itemView.findViewById(R.id.iv_meal_icon)
        val btnBuy : Button = itemView.findViewById(R.id.btn_buy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setNewMealImage(list[position], holder.ivMeal)

        holder.btnBuy.setOnClickListener {
            list[position].num += 1
            JSONHelper().setMeal(list, context)
            Toast.makeText(context, list[position].title + " +1", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setNewMealImage(meal:Meal, ivMeal : ImageView){
        ivMeal.setImageDrawable(context.resources.getDrawable(
            context.resources.getIdentifier(
                meal.title, "drawable", context.packageName), null))
    }

}