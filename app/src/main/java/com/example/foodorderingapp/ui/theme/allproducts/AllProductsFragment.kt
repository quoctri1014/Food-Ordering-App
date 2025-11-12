package com.example.foodorderingapp.ui.theme.allproducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.foodorderingapp.data.MockData
import com.example.foodorderingapp.ui.theme.FoodOrderingAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AllProductsFragment : Fragment() {

    private var sectionTitle: String? = null
    private val allProducts: List<com.example.foodorderingapp.data.Product> = MockData.sampleProducts // Lấy data đầy đủ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sectionTitle = it.getString(ARG_SECTION_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FoodOrderingAppTheme {
                    AllProductsScreen(
                        title = sectionTitle ?: "Tất cả Món ăn",
                        products = allProducts,
                        onBack = { parentFragmentManager.popBackStack() }
                    )
                }
            }
        }
    }

    companion object {
        private const val ARG_SECTION_TITLE = "section_title"

        fun newInstance(sectionTitle: String): AllProductsFragment {
            return AllProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SECTION_TITLE, sectionTitle)
                }
            }
        }
    }
}

// ------------------- COMPOSABLES -------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        }
    )
}

@Composable
fun AllProductsScreen(
    title: String,
    products: List<com.example.foodorderingapp.data.Product>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { SimpleAppBar(title = title, onBack = onBack) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ProductList(
            products = products,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ProductList(products: List<com.example.foodorderingapp.data.Product>, modifier: Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(products) { product ->
            Text(
                text = "${product.name} - ${product.price} đ",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            HorizontalDivider()
        }
    }
}