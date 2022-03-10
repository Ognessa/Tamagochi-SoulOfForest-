package carrira.elan.tamagotchi.other

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carrira.elan.tamagotchi.JSONHelper
import carrira.elan.tamagotchi.R
import carrira.elan.tamagotchi.RVAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShopActivity : AppCompatActivity() {

    lateinit var fabToPrev : FloatingActionButton
    lateinit var rvMealList : RecyclerView
    lateinit var adapter: RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        fabToPrev = findViewById(R.id.fab_to_prev)
        rvMealList = findViewById(R.id.rv_meal_list)
        adapter = RVAdapter(JSONHelper().getMeal(this), this)

        rvMealList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvMealList.adapter = adapter

        fabToPrev.setOnClickListener {
            finish()
        }

    }
}