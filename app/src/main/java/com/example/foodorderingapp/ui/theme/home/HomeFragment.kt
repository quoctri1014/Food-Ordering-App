package com.example.foodorderingapp.ui.theme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.data.MockData
import com.example.foodorderingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHomeRecyclerView()
    }

    private fun setupHomeRecyclerView() {
        val homeAdapter = HomeAdapter()
        binding.rvHomeContent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        val homeContentList = listOf(
            HomeItem.SearchBar,
            HomeItem.Banner(MockData.sampleBanners),
            HomeItem.HorizontalProductSection("Our trusted picks", MockData.sampleProducts), // Danh sách cuộn ngang

        )

        homeAdapter.submitList(homeContentList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}