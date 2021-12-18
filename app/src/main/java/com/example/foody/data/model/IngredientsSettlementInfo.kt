package com.example.foody.data.model

import com.example.foody.data.database.entities.ShoppingCartIngreEntity
import java.math.BigDecimal
import java.nio.ByteOrder


data class IngredientsSettlementInfo(
    var shoppingCartIngre: ShoppingCartIngreEntity,
    var unitPrice: BigDecimal = BigDecimal(0),
    var supplier: String = "unknown"
)
