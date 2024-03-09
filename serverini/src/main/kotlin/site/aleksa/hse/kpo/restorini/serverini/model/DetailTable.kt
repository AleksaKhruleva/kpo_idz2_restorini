package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table

/**
 * Database Model Table: DETAIL
 */
object DetailTable : Table("DETAIL") {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id") references OrderTable.id
    val menuItemId = integer("menu_item_id") references MenuTable.id

    override val primaryKey = PrimaryKey(id)
}
