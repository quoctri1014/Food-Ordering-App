package com.example.foodapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(

    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    popularFoods: List<String> = listOf(
        "Pizza Hải Sản",
        "Mì Ý Bò Băm",
        "Sushi Set Lớn",
        "Trà Sữa Trân Châu",
        "Hamburger Bò Phô Mai",
        "Gà Rán Giòn Tan"
    )
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    var showSuggestions by remember { mutableStateOf(query.text.isEmpty() && popularFoods.isNotEmpty()) }

    Column(modifier = modifier.fillMaxWidth()) {
        // 🔍 Ô nhập tìm kiếm
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it.text)
                // Cập nhật trạng thái hiển thị gợi ý
                showSuggestions = it.text.isEmpty() && popularFoods.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Tìm món ăn...") }, // Lỗi chính tả tiếng Việt (Type in word 'Nhập') có thể nằm trong placeholder nếu dùng 'Nhập...'
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.text.isNotEmpty()) {
                    IconButton(onClick = {
                        query = TextFieldValue("")
                        onSearch("")
                        showSuggestions = true // Hiển thị lại gợi ý sau khi xóa
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        // 💡 Gợi ý món ăn phổ biến (hiện khi thanh tìm kiếm trống)
        if (showSuggestions && popularFoods.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = { showSuggestions = false },

            ) {
                // Thêm Padding Top để Popup không che mất TextField
                Spacer(modifier = Modifier.height(65.dp)) // Độ cao ước tính của TextField + padding
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🔥 Món ăn phổ biến",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Divider(color = Color(0xFFE0E0E0))
                        popularFoods.forEach { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        query = TextFieldValue(suggestion)
                                        showSuggestions = false
                                        onSuggestionClick(suggestion)
                                        onSearch(suggestion)
                                    }
                                    .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}