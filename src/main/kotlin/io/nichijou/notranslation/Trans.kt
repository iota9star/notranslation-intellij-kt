package io.nichijou.notranslation

data class Trans(
    val `data`: List<KV>,
    val errno: Int
)

data class KV(
    val k: String,
    val v: String
)