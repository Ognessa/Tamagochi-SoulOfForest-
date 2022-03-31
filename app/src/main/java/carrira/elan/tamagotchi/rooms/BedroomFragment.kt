package carrira.elan.tamagotchi.rooms

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import carrira.elan.tamagotchi.JSONHelper
import carrira.elan.tamagotchi.R

class BedroomFragment : Fragment() {

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bedroom, container, false)
        val userInterface : View = requireActivity().findViewById(R.id.lu_interface)

        val ivMushLight : ImageView = view.findViewById(R.id.mushroms_light)
        val ivLamp : ImageView = view.findViewById(R.id.iv_lamp)

        ivLamp.setOnClickListener {
            if(JSONHelper().getSleepCoeff(requireContext()) == -1){
                userInterface.background = view.resources.getDrawable(R.drawable.night_shadow, null)
                ivMushLight.visibility = View.INVISIBLE
                JSONHelper().setSleepCoeff(10, requireContext())
            }else{
                userInterface.background = null
                ivMushLight.visibility = View.VISIBLE
                JSONHelper().setSleepCoeff(-1, requireContext())
            }
        }

        return view
    }

    companion object {
        fun newInstance() =
            BedroomFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}