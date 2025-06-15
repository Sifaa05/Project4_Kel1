package com.example.billbuddy.util

import com.example.billbuddy.data.Item
import java.util.UUID

data class ItemOperationResult(
    val success: Boolean,
    val message: String,
    val updatedItems: List<Item>
)
fun addOrUpdateItem(
    items: MutableList<Item>,
    itemName: String,
    quantity: Int,
    unitPrice: String,
    isEditing: Boolean,
    editingItemId: String?
): ItemOperationResult {
    if (itemName.isEmpty() || quantity <= 0 || unitPrice.isEmpty()) {
        return ItemOperationResult(
            success = false,
            message = "Please fill in all item fields",
            updatedItems = items.toList()
        )
    }

    val unitPriceValue = unitPrice.toLongOrNull() ?: 0
    val totalPriceValue = unitPriceValue * quantity

    val newItems = items.toMutableList()

    if (isEditing && editingItemId != null) {
        val index = newItems.indexOfFirst { it.itemId == editingItemId }
        if (index != -1) {
            newItems[index] = Item(
                itemId = editingItemId,
                name = itemName,
                quantity = quantity,
                unitPrice = unitPriceValue,
                totalPrice = totalPriceValue
            )
            return ItemOperationResult(
                success = true,
                message = "Item $itemName updated",
                updatedItems = newItems
            )
        }
    } else {
        newItems.add(
            Item(
                itemId = UUID.randomUUID().toString(),
                name = itemName,
                quantity = quantity,
                unitPrice = unitPriceValue,
                totalPrice = totalPriceValue
            )
        )
        return ItemOperationResult(
            success = true,
            message = "Item $itemName added",
            updatedItems = newItems
        )
    }

    return ItemOperationResult(
        success = false,
        message = "Failed to process item",
        updatedItems = items.toList()
    )
}