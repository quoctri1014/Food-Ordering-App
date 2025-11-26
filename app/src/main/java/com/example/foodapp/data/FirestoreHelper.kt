package com.example.foodapp.data

import android.util.Log
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.OrderItem
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.data.model.Voucher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- 1. USER MANAGEMENT (QUẢN LÝ NGƯỜI DÙNG) ---

    fun syncUser(user: FirebaseUser, username: String? = null) {
        val userId = user.uid

        // Tạo map dữ liệu để lưu
        val userData = hashMapOf<String, Any>(
            "email" to (user.email ?: ""),
            "id" to userId
        )

        // 1. Lưu tên người dùng
        if (!username.isNullOrEmpty()) {
            userData["username"] = username
        } else if (!user.displayName.isNullOrEmpty()) {
            userData["username"] = user.displayName!!
        }

        // 2. Lưu số điện thoại
        if (!user.phoneNumber.isNullOrEmpty()) {
            userData["phoneNumber"] = user.phoneNumber!!
        }

        // 3. Đẩy lên Firestore (merge để giữ lại dữ liệu cũ như address)
        db.collection("users").document(userId).set(userData, SetOptions.merge())
    }

    suspend fun saveAddress(userId: String, newAddress: String): Result<Unit> {
        return try {
            db.collection("users").document(userId).update("address", newAddress).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun saveContactInfo(userId: String, newAddress: String, newPhone: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "address" to newAddress,
                "phoneNumber" to newPhone
            )
            db.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ⭐ HÀM MỚI: CẬP NHẬT HỒ SƠ (TÊN, SĐT, AVATAR) ⭐
    suspend fun updateUserProfile(userId: String, name: String, phone: String, avatarUrl: String): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "username" to name,
                "phoneNumber" to phone,
                "avatarUrl" to avatarUrl
            )
            db.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                val address = snapshot.getString("address") ?: ""
                val username = snapshot.getString("username") ?: ""
                val email = snapshot.getString("email") ?: ""
                val phone = snapshot.getString("phoneNumber") ?: ""
                val role = snapshot.getString("role") ?: "user"
                val avatar = snapshot.getString("avatarUrl") ?: ""

                User(id = userId, username = username, email = email, phoneNumber = phone, address = address, role = role, avatarUrl = avatar)
            } else null
        } catch (e: Exception) { null }
    }

    // --- 2. CART MANAGEMENT (QUẢN LÝ GIỎ HÀNG) ---
    fun getCart(onResult: (List<CartItem>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart").get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                    val food = doc.toObject(Food::class.java)
                    val quantity = doc.getLong("cartQuantity")?.toInt() ?: 1
                    val note = doc.getString("note") ?: ""
                    if (food != null) CartItem(food, quantity, note) else null
                }
                onResult(items)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateCartItem(cartItem: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        val itemData = hashMapOf(
            "id" to cartItem.food.id, "name" to cartItem.food.name, "price" to cartItem.food.price,
            "imageUrl" to cartItem.food.imageUrl, "rating" to cartItem.food.rating, "description" to cartItem.food.description,
            "time" to cartItem.food.time, "kCal" to cartItem.food.kCal, "categoryId" to cartItem.food.categoryId,
            "cartQuantity" to cartItem.quantity,
            "note" to cartItem.note
        )
        db.collection("users").document(userId).collection("cart").document(cartItem.food.id).set(itemData as Map<String, Any>)
    }

    fun removeCartItem(foodId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart").document(foodId).delete()
    }

    fun clearCart() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart").get().addOnSuccessListener { snapshot ->
            for (doc in snapshot) { doc.reference.delete() }
        }
    }

    // --- 3. FAVORITES (YÊU THÍCH) ---
    fun getFavorites(onResult: (List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("favorites").get()
            .addOnSuccessListener { snapshot -> onResult(snapshot.documents.map { it.id }) }
    }

    fun toggleFavorite(food: Food, isAdd: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val ref = db.collection("users").document(userId).collection("favorites").document(food.id)
        if (isAdd) ref.set(food) else ref.delete()
    }

    // --- 4. ORDER MANAGEMENT (QUẢN LÝ ĐƠN HÀNG) ---

    suspend fun saveOrder(cartItems: List<CartItem>, paymentInfo: PaymentInfo, subtotal: Int): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No user"))

        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                foodId = cartItem.food.id,
                foodName = cartItem.food.name,
                foodImage = cartItem.food.imageUrl,
                price = cartItem.food.price,
                quantity = cartItem.quantity,
                note = cartItem.note
            )
        }

        val orderId = db.collection("all_orders").document().id

        val newOrder = Order(
            id = orderId,
            userId = userId,
            userName = paymentInfo.fullName,
            address = paymentInfo.address,
            phone = paymentInfo.phone,
            totalPrice = subtotal,
            shippingFee = paymentInfo.shippingFee,
            finalAmount = subtotal + paymentInfo.shippingFee,
            paymentMethod = paymentInfo.method.displayName,
            status = "Đang xử lý",
            items = orderItems
        )

        return try {
            db.collection("all_orders").document(orderId).set(newOrder).await()
            db.collection("users").document(userId).collection("orders").document(orderId).set(newOrder).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserOrders(): List<Order> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()

            snapshot.documents.mapNotNull { doc ->
                val order = doc.toObject(Order::class.java)
                order?.copy(id = doc.id)
            }
        } catch (e: Exception) { emptyList() }
    }

    fun listenToUserOrders(userId: String, onUpdate: (List<Order>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        val order = doc.toObject(Order::class.java)
                        order?.copy(id = doc.id)
                    }
                    onUpdate(orders)
                }
            }
    }

    fun listenToLatestOrder(userId: String, onUpdate: (Order?) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                    onUpdate(order)
                } else {
                    onUpdate(null)
                }
            }
    }

    // --- 5. ADMIN FUNCTIONS ---

    suspend fun getAllOrders(): List<Order> {
        return try {
            val snapshot = db.collection("all_orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()
            snapshot.toObjects(Order::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean {
        return try {
            db.collection("all_orders").document(orderId).update("status", newStatus).await()
            val orderSnapshot = db.collection("all_orders").document(orderId).get().await()
            val userId = orderSnapshot.getString("userId")
            if (userId != null) {
                db.collection("users").document(userId).collection("orders").document(orderId)
                    .update("status", newStatus).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addVoucher(code: String, discount: String, expiry: String, condition: String): Boolean {
        val voucherData = hashMapOf(
            "code" to code,
            "discount" to discount,
            "expiry" to expiry,
            "condition" to condition
        )
        return try {
            db.collection("vouchers").document(code).set(voucherData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- 6. DATA SOURCE ---
    fun getAllFoods(onResult: (List<Food>) -> Unit) {
        db.collection("foods").get().addOnSuccessListener { onResult(it.toObjects(Food::class.java)) }.addOnFailureListener { onResult(emptyList()) }
    }

    // --- 7. VOUCHER MANAGEMENT ---
    fun getAllVouchers(onResult: (List<Voucher>) -> Unit) {
        db.collection("vouchers").get()
            .addOnSuccessListener { snapshot ->
                val vouchers = snapshot.toObjects(Voucher::class.java)
                onResult(vouchers)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun checkVoucher(code: String, onResult: (Voucher?) -> Unit) {
        db.collection("vouchers").document(code).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onResult(doc.toObject(Voucher::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun uploadMockData() { /* Giữ nguyên mock data nếu bạn có */ }
}