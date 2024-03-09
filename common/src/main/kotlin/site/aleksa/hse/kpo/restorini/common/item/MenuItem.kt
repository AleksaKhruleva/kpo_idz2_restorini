package site.aleksa.hse.kpo.restorini.common.item

import kotlinx.serialization.Serializable

/**
 * Menu Item data class
 */
@Serializable
data class MenuItem(
    val id: Int,
    val fromDate: String,
    val toDate: String? = null,
    val type: String,
    val price: Int,
    val quantity: Long = Long.MAX_VALUE,
    val cotime: Int,
    val title: String,
    val description: String
)
