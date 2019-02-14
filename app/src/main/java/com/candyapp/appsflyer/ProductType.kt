package com.candyapp.appsflyer

enum class ProductType {
    MM,
    Skittles,
    Xiaomi,
    iPhone,
    WhiteShoes,
    RedShoes,
    BlackShoes;

    companion object {
        fun fromAd(ad: String?): ProductType?{
            return when(ad?.toLowerCase()) {
                "mm" -> ProductType.MM
                "skittles" -> ProductType.Skittles
                "xiaomi" -> ProductType.Xiaomi
                "iphone" -> ProductType.iPhone
                "whiteshoes" -> ProductType.WhiteShoes
                "redshoes" -> ProductType.RedShoes
                "blackshoes" -> ProductType.BlackShoes
                else -> null
            }
        }
    }
}