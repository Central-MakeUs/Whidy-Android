package com.whidy.whidyandroid.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = requireNotNull(_binding){"FragmentLoginBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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