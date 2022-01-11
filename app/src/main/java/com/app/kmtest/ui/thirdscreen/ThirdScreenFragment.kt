package com.app.kmtest.ui.thirdscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.kmtest.R
import com.app.kmtest.adapter.UserAdapter
import com.app.kmtest.data.ApiResponse
import com.app.kmtest.model.Data
import com.app.kmtest.databinding.FragmentThirdScreenBinding
import com.app.kmtest.util.Constants.QUERY_PAGE_SIZE

class ThirdScreenFragment : Fragment() {

    private lateinit var viewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ThirdScreenFragmentArgs>()
    var page = 1
    var isLoading = false
    var totalLoadPage = 1

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && page < totalLoadPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem
            if (shouldPaginate) {
                page++
                getUserData(false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        // Init viewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[UserViewModel::class.java]

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

            // Get User Name
            val userName = args.userName

            // Init Adapter and RecyclerView
            setupRecyclerView()

            // Show progressbar
            showProgressBar(true)

            // Get User List
            getUserData(true)

            // Navigate back to Selection Page
            userName?.let { onItemSelected(it) }

            // Get data when refresh page
            swipeRefreshLayout = binding.swipeRefreshLayout
            swipeRefreshLayout.setOnRefreshListener {
                userAdapter.clear()
                page = 1
                getUserData(true)
            }
        }
    }

    // Init and setup recyclerview and adapter
    private fun setupRecyclerView() {
        userAdapter = UserAdapter()
        with(binding.rvUser) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            addOnScrollListener(this@ThirdScreenFragment.scrollListener)
        }
    }

    // Fetch user data from API
    private fun getUserData(isRefresh: Boolean) {
        isLoading = true
        if (!isRefresh) showProgressBar(true)
        viewModel.getListUser(page, QUERY_PAGE_SIZE).observe(viewLifecycleOwner, { response ->
            if (response != null) when (response) {
                is ApiResponse.Success -> {
                    showProgressBar(false)
                    if (response.data.isEmpty()) {
                        Toast.makeText(
                            context,
                            "There's no data",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    userAdapter.setData(response.data as ArrayList<Data>)
                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                }

                is ApiResponse.Error -> {
                    showProgressBar(false)
                    Toast.makeText(
                        context,
                        "An error occurred:  ${response.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                }

                is ApiResponse.Empty -> {
                    showProgressBar(false)
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                }
            }
            viewModel.totalPage.observe(viewLifecycleOwner, {
                totalLoadPage = it
            })
        })
    }

    // Navigate back to Second Screen
    private fun onItemSelected(name: String) {
        userAdapter.setOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onItemClicked(user: Data) {
                val actionToSecondScreen =
                    ThirdScreenFragmentDirections.actionThirdScreenFragmentToSecondScreenFragment(
                        name,
                        requireContext().getString(
                            R.string.user_name,
                            user.firstName,
                            user.lastName
                        )
                    )
                findNavController().navigate(actionToSecondScreen)
            }
        })
    }

    /**
     * Change the visibility of progressBar
     * true --> Show progressBar
     * false --> Hide progressBar
     */
    private fun showProgressBar(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE
        else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}