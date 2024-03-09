package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table

/**
 * Database Model Table: REVIEW
 */
object ReviewTable : Table("REVIEW") {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id") references OrderTable.id
    val menuItemId = integer("menu_item_id") references MenuTable.id
    val rating = integer("rating").default(0)
    val comment = varchar("comment", 1024)

    override val primaryKey = PrimaryKey(id)
}
