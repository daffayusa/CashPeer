package com.example.cashpeer.feature.savinggoal.domain.model

fun getSavingGoalEmoji(name: String): String {
    val lower = name.lowercase()
    return when {
        "laptop" in lower || "komputer" in lower || "pc" in lower || "macbook" in lower -> "💻"
        "hp" in lower || "handphone" in lower || "phone" in lower || "gadget" in lower || "iphone" in lower || "android" in lower -> "📱"
        "mobil" in lower || "car" in lower -> "🚗"
        "motor" in lower || "vespa" in lower -> "🏍️"
        "rumah" in lower || "kost" in lower || "kos" in lower || "renovasi" in lower || "apartemen" in lower || "tanah" in lower -> "🏠"
        "nikah" in lower || "kawin" in lower || "wedding" in lower -> "💍"
        "liburan" in lower || "travel" in lower || "jalan" in lower || "trip" in lower || "wisata" in lower || "holiday" in lower || "healing" in lower -> "✈️"
        "darurat" in lower || "emergency" in lower -> "🛡️"
        "kuliah" in lower || "sekolah" in lower || "kursus" in lower || "pendidikan" in lower || "buku" in lower || "wisuda" in lower -> "🎓"
        "investasi" in lower || "saham" in lower || "reksadana" in lower || "crypto" in lower || "emas" in lower || "gold" in lower -> "📈"
        "haji" in lower || "umroh" in lower || "umrah" in lower -> "🕋"
        "kamera" in lower || "photo" in lower || "dslr" in lower -> "📷"
        "nonton" in lower || "konser" in lower -> "🎟️"
        "sepeda" in lower -> "🚲"
        "baju" in lower || "sepatu" in lower || "tas" in lower || "belanja" in lower -> "🛍️"
        "kurban" in lower || "qurban" in lower -> "🐑"
        "kesehatan" in lower || "obat" in lower || "medis" in lower -> "💊"
        "game" in lower || "gaming" in lower || "playstation" in lower || "ps5" in lower || "nintendo" in lower -> "🎮"
        else -> "🎯"
    }
}
