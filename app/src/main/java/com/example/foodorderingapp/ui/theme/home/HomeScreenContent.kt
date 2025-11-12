package com.example.foodorderingapp.ui.theme.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// --- Các Composable cho từng loại item ---

@Composable
fun SearchBarComposable() {
    Text(
        text = "Tìm kiếm món ăn...",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun BannerComposable(banner: HomeItem.Banner) {
    Text(
        text = "Banner: ${banner.imageUrls.size} hình ảnh",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun HorizontalSectionComposable(
    section: HomeItem.HorizontalProductSection,
    onViewAllClick: () -> Unit // Tham số callback cho sự kiện click
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleLarge
            )

            // Nút "View all" có thể click được
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onViewAllClick) // 👈 Gắn sự kiện click
            )
        }

        // LazyRow cho sản phẩm cuộn ngang
        Text(
            text = "Danh sách ${section.products.size} sản phẩm (cuộn ngang)",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun CategorySectionComposable() {
    Text(text = "Category Section Placeholder", modifier = Modifier.padding(16.dp))
}


// --- Hàm Composable Chính (HomeScreenContent) ---

@Composable
fun HomeScreenContent(
    homeItems: List<HomeItem>,
    onViewAllClick: (sectionTitle: String) -> Unit // Tham số callback nhận tên Section
) {
    LazyColumn {
        items(homeItems) { item ->
            when (item) {
                is HomeItem.SearchBar -> SearchBarComposable()
                is HomeItem.Banner -> BannerComposable(item)
                is HomeItem.HorizontalProductSection -> {
                    // Truyền callback xuống và gửi tên Section khi click
                    HorizontalSectionComposable(
                        section = item,
                        onViewAllClick = { onViewAllClick(item.title) }
                    )
                }
                is HomeItem.CategorySection -> CategorySectionComposable()
            }
        }
    }
}