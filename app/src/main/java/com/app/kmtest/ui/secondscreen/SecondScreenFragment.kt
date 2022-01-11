package com.app.kmtest.ui.secondscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.kmtest.databinding.FragmentSecondScreenBinding

class SecondScreenFragment : Fragment() {

    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SecondScreenFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {

            // Init Toolbar
            val toolbar = binding.toolbar

            // Set Navigate to previous page (backstack)
            toolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }

            // Get Data
            val userName = args.userName
            val selectedUser = args.selectedUser

            // Populate Data
            populateData(userName, selectedUser)

            // Navigate to Third Screen
            binding.btnChooseUser.setOnClickListener {
                val actionToThird =
                    SecondScreenFragmentDirections.actionSecondScreenFragmentToThirdScreenFragment(
                        userName
                    )
                findNavController().navigate(actionToThird)
            }
        }
    }

    // Populate and set data from previous page
    private fun populateData(userName: String?, selectedUser: String?) {
        with(binding) {
            if (userName != null && userName.isNotEmpty()) tvUserName.text = userName
            if (selectedUser != null && selectedUser.isNotEmpty()) tvSelectedUser.text =
                selectedUser
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}