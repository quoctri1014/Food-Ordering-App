package com.example.foodorderingapp.ui.theme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.foodorderingapp.R // Đã thêm
import com.example.foodorderingapp.data.MockData
import com.example.foodorderingapp.ui.theme.allproducts.AllProductsFragment

// Thay thế HomeFragment bằng Compose Fragment
class HomeComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navigateToAllProductsScreen: (String) -> Unit = { sectionTitle ->
            val allProductsFragment = AllProductsFragment.newInstance(sectionTitle)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, allProductsFragment)
                .addToBackStack(null)
                .commit()
        }

        return ComposeView(requireContext()).apply {
            setContent {

                val homeContentList = listOf(
                    HomeItem.SearchBar,
                    HomeItem.Banner(MockData.sampleBanners),
                    HomeItem.HorizontalProductSection("Our trusted picks", MockData.sampleProducts),
                    HomeItem.HorizontalProductSection("More to love", MockData.sampleProducts),
                )

                HomeScreenContent(
                    homeItems = homeContentList,
                    onViewAllClick = navigateToAllProductsScreen
                )
            }
        }
    }
}