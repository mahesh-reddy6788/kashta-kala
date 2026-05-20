package com.kashta.kala.data

import com.kashta.kala.R

// ─────────────────────────────────────────────
//  Central hardcoded data store for Kashta Kala
// ─────────────────────────────────────────────

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val imageResId: Int? = null,
    val dimensions: String? = null,
    val material: String? = null,
    val warranty: String? = null,
    val isFeatured: Boolean = false
)

data class Order(
    val id: Int,
    val productName: String,
    val productPrice: Double,
    val status: String, // "Pending" | "Processing" | "Shipped" | "Delivered"
    val orderDate: String,
    val imageResId: Int? = null,
    val quantity: Int = 1,
    val isCustomOrder: Boolean = false
)

data class Quote(
    val id: Int,
    val furnitureName: String,
    val woodType: String,
    val length: Double,
    val width: Double,
    val height: Double,
    val thickness: Double,
    val materialCost: Double,
    val designFee: Double,
    val customFinish: Boolean,
    val totalEstimate: Double,
    val quoteDate: String,
    val status: String = "Draft"
)

data class FavoriteItem(
    val productId: Int,
    val name: String,
    val category: String,
    val imageResId: Int? = null,
    val price: Double = 0.0
)

data class UserAccount(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val isAdmin: Boolean = false
)

object DataRepository {

    // ── Users ─────────────────────────────────
    val usersList: MutableList<UserAccount> = mutableListOf(
        UserAccount(1, "Admin",          "admin@kashtakala.com", "0000000000", "admin123", true),
        UserAccount(2, "User",           "user@kashtakala.com",  "9876543210", "user123", false),
        UserAccount(3, "Chintan Sharma", "chintan@gmail.com",    "9123456780", "pass123", false),
        UserAccount(4, "Priya Mehta",    "priya@gmail.com",      "9988776655", "pass123", false),
        UserAccount(5, "Rahul Verma",    "rahul@gmail.com",      "9871234560", "pass123", false)
    )

    // ── Products ──────────────────────────────
    val products: MutableList<Product> = mutableListOf(
        Product(
            id = 1,
            name = "Haveli Solid Teak Sofa",
            category = "Sofas",
            price = 45000.0,
            description = "A majestic 3-seater sofa crafted from premium solid teak. Features hand-carved armrests, high-density foam cushions, and a rich walnut finish that deepens with age.",
            imageResId = R.drawable.royal_velvet_sofa,
            dimensions = "7 × 3 × 3.5 ft",
            material = "Solid Teak",
            warranty = "3 Years",
            isFeatured = true
        ),
        Product(
            id = 2,
            name = "Rafter Queen Bed Frame",
            category = "Beds",
            price = 32000.0,
            description = "A sturdy queen-size bed frame in Sheesham wood with a slatted headboard and under-bed storage drawers. Built to last generations.",
            imageResId = R.drawable.imperial_king_bed,
            dimensions = "6.5 × 5 × 4 ft",
            material = "Sheesham",
            warranty = "2 Years",
            isFeatured = true
        ),
        Product(
            id = 3,
            name = "Khaas Dining Table (6-Seater)",
            category = "Dining Tables",
            price = 28000.0,
            description = "A warm mango wood dining table with a live-edge top and tapered legs. Seats 6 comfortably. Each piece is unique due to the natural grain.",
            imageResId = R.drawable.modern_oak_table,
            dimensions = "6 × 3 × 2.5 ft",
            material = "Mango Wood",
            warranty = "2 Years",
            isFeatured = false
        ),
        Product(
            id = 4,
            name = "Craftline Office Chair",
            category = "Chairs",
            price = 8500.0,
            description = "An ergonomic office chair with a solid plywood frame, foam padding, and breathable fabric upholstery. Adjustable height and tilt mechanism.",
            imageResId = R.drawable.ergonomic_office_chair,
            dimensions = "2 × 2 × 3.5 ft",
            material = "Plywood + Foam",
            warranty = "1 Year",
            isFeatured = false
        ),
        Product(
            id = 5,
            name = "Summit Wardrobe (3-Door)",
            category = "Wardrobes",
            price = 22000.0,
            description = "A spacious 3-door wardrobe combining MDF panels with Sheesham wood accents. Includes a full-length mirror, hanging rail, and 4 shelves.",
            imageResId = R.drawable.classic_wardrobe,
            dimensions = "6 × 2 × 7 ft",
            material = "MDF + Sheesham",
            warranty = "2 Years",
            isFeatured = true
        ),
        Product(
            id = 6,
            name = "Kacha Study Desk",
            category = "Office Furniture",
            price = 12000.0,
            description = "A minimalist pine wood study desk with a cable management groove, two side drawers, and a smooth lacquer finish. Perfect for home offices.",
            imageResId = R.drawable.luxury_coffee_table,
            dimensions = "4 × 2 × 2.5 ft",
            material = "Pine",
            warranty = "1 Year",
            isFeatured = false
        ),
        Product(
            id = 7,
            name = "Aranya Lounge Chair",
            category = "Chairs",
            price = 14500.0,
            description = "A sculptural lounge chair in solid teak with hand-woven cane back and seat. Inspired by mid-century Indian craftsmanship.",
            imageResId = R.drawable.ergonomic_office_chair,
            dimensions = "2.5 × 2.5 × 3 ft",
            material = "Solid Teak + Cane",
            warranty = "2 Years",
            isFeatured = false
        ),
        Product(
            id = 8,
            name = "Neem Wood Coffee Table",
            category = "Dining Tables",
            price = 9800.0,
            description = "A compact coffee table in neem wood with a lower shelf for storage. The natural grain and warm honey tone make it a living room centrepiece.",
            imageResId = R.drawable.luxury_coffee_table,
            dimensions = "3.5 × 2 × 1.5 ft",
            material = "Neem Wood",
            warranty = "1 Year",
            isFeatured = false
        )
    )

    // ── Orders (sample for logged-in user) ────
    val orders: MutableList<Order> = mutableListOf(
        Order(
            id = 1001,
            productName = "Haveli Solid Teak Sofa",
            productPrice = 45000.0,
            status = "Delivered",
            orderDate = "02 Mar 2025",
            imageResId = R.drawable.royal_velvet_sofa,
            quantity = 1
        ),
        Order(
            id = 1002,
            productName = "Rafter Queen Bed Frame",
            productPrice = 32000.0,
            status = "Shipped",
            orderDate = "10 Apr 2025",
            imageResId = R.drawable.imperial_king_bed,
            quantity = 1
        ),
        Order(
            id = 1003,
            productName = "Custom Corner Desk",
            productPrice = 18500.0,
            status = "Processing",
            orderDate = "05 May 2025",
            imageResId = null,
            quantity = 1,
            isCustomOrder = true
        ),
        Order(
            id = 1004,
            productName = "Craftline Office Chair",
            productPrice = 8500.0,
            status = "Pending",
            orderDate = "14 May 2025",
            imageResId = R.drawable.ergonomic_office_chair,
            quantity = 2
        )
    )

    // ── Quotes (sample saved quotes) ──────────
    val quotes: MutableList<Quote> = mutableListOf(
        Quote(
            id = 1,
            furnitureName = "Corner Study Desk",
            woodType = "Teak",
            length = 5.0,
            width = 2.5,
            height = 2.5,
            thickness = 1.5,
            materialCost = 3500.0,
            designFee = 1200.0,
            customFinish = false,
            totalEstimate = 4700.0,
            quoteDate = "10 Apr 2025",
            status = "Submitted"
        ),
        Quote(
            id = 2,
            furnitureName = "Bookshelf Unit",
            woodType = "Sheesham",
            length = 4.0,
            width = 1.0,
            height = 6.0,
            thickness = 1.0,
            materialCost = 2640.0,
            designFee = 800.0,
            customFinish = true,
            totalEstimate = 4048.8,
            quoteDate = "28 Apr 2025",
            status = "Draft"
        )
    )

    // ── Wishlist (sample) ─────────────────────
    val wishlist: MutableList<FavoriteItem> = mutableListOf(
        FavoriteItem(
            productId = 3,
            name = "Khaas Dining Table (6-Seater)",
            category = "Dining Tables",
            imageResId = R.drawable.modern_oak_table,
            price = 28000.0
        ),
        FavoriteItem(
            productId = 7,
            name = "Aranya Lounge Chair",
            category = "Chairs",
            imageResId = R.drawable.ergonomic_office_chair,
            price = 14500.0
        )
    )

    // ── Categories ────────────────────────────
    val categories = listOf(
        "Sofas", "Beds", "Dining Tables", "Chairs", "Wardrobes", "Office Furniture"
    )

    // ── Wood types & prices ───────────────────
    val woodTypes = listOf(
        "Teak" to 280.0,
        "Sheesham" to 220.0,
        "Pine" to 150.0,
        "Plywood" to 90.0,
        "MDF" to 70.0,
        "Mango Wood" to 180.0
    )

    // ── Helpers ───────────────────────────────
    fun getProductById(id: Int): Product? = products.find { it.id == id }

    fun getFeaturedProducts(): List<Product> = products.filter { it.isFeatured }

    fun getProductsByCategory(category: String): List<Product> =
        if (category == "All") products else products.filter { it.category == category }

    fun searchProducts(query: String): List<Product> =
        products.filter { it.name.contains(query, ignoreCase = true) }

    fun isInWishlist(productId: Int): Boolean = wishlist.any { it.productId == productId }

    fun addToWishlist(product: Product) {
        if (!isInWishlist(product.id)) {
            wishlist.add(
                FavoriteItem(
                    productId = product.id,
                    name = product.name,
                    category = product.category,
                    imageResId = product.imageResId,
                    price = product.price
                )
            )
        }
    }

    fun removeFromWishlist(productId: Int) {
        wishlist.removeAll { it.productId == productId }
    }

    fun addOrder(order: Order) {
        orders.add(0, order)
    }

    fun addQuote(quote: Quote) {
        quotes.add(0, quote)
    }

    fun deleteQuote(quoteId: Int) {
        quotes.removeAll { it.id == quoteId }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            orders[index] = orders[index].copy(status = newStatus)
        }
    }

    fun nextOrderId(): Int = (orders.maxOfOrNull { it.id } ?: 1000) + 1
    fun nextQuoteId(): Int = (quotes.maxOfOrNull { it.id } ?: 0) + 1

    fun registerNewUser(name: String, email: String, phone: String, password: String): Boolean {
        if (usersList.any { it.email.equals(email, ignoreCase = true) }) {
            return false // Email already exists
        }
        val nextId = (usersList.maxOfOrNull { it.id } ?: 0) + 1
        usersList.add(UserAccount(nextId, name, email, phone, password))
        return true
    }
}
