package com.whidy.whidyandroid.presentation.map.add

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceAddBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import timber.log.Timber

class PlaceAddFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceAddBinding? = null
    private val binding: FragmentPlaceAddBinding
        get() = requireNotNull(_binding){"FragmentPlaceAddBinding -> null"}

    private val viewModel: PlaceAddViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                when (result.resultCode) {
                    // ADDR_RESULT : 1001
                    1001 -> {
                        val data = result.data!!.getStringExtra("data")
                        Timber.d("Received address: $data")
                        if (!data.isNullOrEmpty()) {
                            binding.tvPlaceAddress.text = data
                            viewModel.setAddress(data)
                            updateCompleteButtonState()
                        }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.tvPlaceAddress.setOnClickListener {
            val intent = Intent(requireContext(), AddressActivity::class.java)
            launcher.launch(intent)
        }

        binding.btnAddressSearch.setOnClickListener {
            val intent = Intent(requireContext(), AddressActivity::class.java)
            launcher.launch(intent)
        }

        binding.etPlaceName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCompleteButtonState()
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.etPlaceName.setBackgroundResource(R.drawable.bg_input_place_add_default)
                } else {
                    binding.etPlaceName.setBackgroundResource(R.drawable.bg_input_place_add_active)
                }
            }
        })

        binding.btnPlaceAddComplete.setOnClickListener {
            val placeName = binding.etPlaceName.text.toString().trim()
            val addressText = binding.tvPlaceAddress.text.toString().trim()
            if (placeName.isNotEmpty() && addressText.isNotEmpty()) {
                viewModel.postPlaceRequest(placeName,
                    onSuccess = {
                        val bundle = Bundle().apply {
                            putBoolean("showPlaceAddSuccessDialog", true)
                        }
                        navController.navigate(R.id.action_navigation_place_add_to_map, bundle)
                    },
                    onError = { error ->
                        Timber.e(error, "Failed to add place")
                    }
                )
            }
        }
    }

    private fun updateCompleteButtonState() {
        val isAddressFilled = binding.tvPlaceAddress.text.toString().trim().isNotEmpty()
        val isPlaceNameFilled = binding.etPlaceName.text.toString().trim().isNotEmpty()
        binding.btnPlaceAddComplete.isEnabled = isAddressFilled && isPlaceNameFilled
        binding.btnPlaceAddComplete.isSelected = isAddressFilled && isPlaceNameFilled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}