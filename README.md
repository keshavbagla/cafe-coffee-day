# Starbucks-Style Mobile Order App

This Android app implements a Starbucks-style mobile ordering interface using Jetpack Compose with the following features:

## Features Implemented

### 🎯 **Complete Menu Categories**
- **Drinks**: Espresso, Frappuccino®, Blended Beverages, Other Beverages
- **Food**: Veg, Paneer, Cheese, Grilled options
- **Merchandise**: Mugs, Tumblers, Coffee Beans, Accessories
- **Coffee At Home**: Whole Bean, Ground, Instant, Pods

### 🛒 **Smart Cart Management**
- Add/Remove items with quantity controls
- Real-time cart state management
- Bottom cart indicator (shows when items are added)
- View Cart screen with order summary
- Automatic price calculation with taxes

### 🎨 **UI Features**
- Starbucks green color scheme (#00704A)
- Order type selection (Dine In / Takeaway)
- Location and delivery time display
- "No Store Found" error state
- Filter chips for menu categories
- Smooth navigation between screens

### 📱 **Screen Components**

#### 1. **Category Screens**
- Dynamic menu loading from Firebase
- Subcategory tabs with active indicators
- Filter chips for menu customization
- Sample data fallback when Firebase is unavailable

#### 2. **Menu Item Cards**
- Product images with fallback placeholders
- Availability indicators (green/red dots)
- Calorie information
- Add/Remove quantity controls
- Price display in Indian Rupees (₹)

#### 3. **Cart System**
- Bottom cart indicator appears when items added
- Shows item count and total amount
- "View Cart" button for detailed cart view
- Order summary with tax calculations

#### 4. **ViewCart Screen**
- Complete cart item list
- Quantity adjustment controls
- Order summary with subtotal, taxes, and total
- Checkout button for order processing
- Empty cart state with "Browse Menu" option

## Technical Implementation

### **Data Models**
```kotlin
data class CafeMenuItem(
    val name: String,
    val price: String,
    val size: String,
    val calories: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val ingredients: String,
    val availability: Boolean
)

data class CartItem(
    val menuItem: CafeMenuItem,
    val quantity: Int
)
```

### **Cart Management**
- Singleton `CartManager` for global cart state
- Real-time UI updates using Compose state
- Automatic price calculations
- Quantity management with add/remove functions

### **Navigation Structure**
```
MainActivity (Compose)
├── DrinksScreen (default)
├── FoodScreen  
├── MerchandiseScreen
├── CoffeeAtHomeScreen
└── ViewCartScreen
```

## Sample Menu Items

### Drinks Category
- **Date Cortado** - ₹383.25 (168 kcal)
- **Churro Frappuccino** - ₹430.50 (368 kcal)  
- **Barista Pride Latte** - ₹430.50 (240 kcal)

### Food Category
- **Veg Sandwich** - ₹250 (280 kcal)
- **Paneer Tikka Wrap** - ₹320 (350 kcal)

## How to Use

1. **Launch App**: Start from existing UserActivity
2. **Browse Menu**: Tap "Browse New Menu" to open MainActivity
3. **Select Category**: Choose from Drinks, Food, Merchandise, or Coffee At Home
4. **Filter Items**: Use subcategory tabs and filter chips
5. **Add Items**: Tap "Add Item" or use +/- controls
6. **View Cart**: Bottom indicator appears, tap "View Cart"
7. **Checkout**: Review order and proceed to checkout

## Firebase Integration

The app supports Firebase Firestore for menu data with the following structure:
```
menu/
├── drinks/
│   └── items/
├── food/
│   └── items/
├── merchandise/
│   └── items/
└── coffee_at_home/
    └── items/
```

## UI Screenshots Matching

The implementation matches the provided Starbucks app screenshots:
- Green header with "Mobile Order and Pay" title
- Location display with time estimate
- Dine In / Takeaway toggle buttons
- Category tabs with active indicators
- Menu items with circular images and availability dots
- Bottom cart indicator with item count and total
- Quantity controls with green background

## Dependencies Added

```kotlin
implementation("androidx.navigation:navigation-compose:2.7.6")
implementation("io.coil-kt:coil-compose:2.5.0")
```

The app now provides a complete Starbucks-style mobile ordering experience with modern UI components and smooth user interactions.