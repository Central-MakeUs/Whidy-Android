package com.whidy.whidyandroid.presentation.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentScrapBinding

class ScrapFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentScrapBinding? = null
    private val binding: FragmentScrapBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScrapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}