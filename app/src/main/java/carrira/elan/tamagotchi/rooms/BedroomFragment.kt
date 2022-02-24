package carrira.elan.tamagotchi.rooms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import carrira.elan.tamagotchi.R

class BedroomFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bedroom, container, false)
    }

    companion object {
        fun newInstance() =
            BedroomFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}