package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

/**
 * Database Model Table: MENU
 */
object MenuTable : Table("MENU") {
    val id = integer("id").autoIncrement()
    val fromDate = date ("from_date").clientDefault { LocalDate.now() }
    val fromAdminId = integer("from_admin_id") references AdminTable.id
    val toDate = date("to_date").nullable()
    val toAdminId = (integer("to_admin_id") references AdminTable.id).nullable()
    val type = char("type", 1).default(" ")
    val price = integer("price")
    val quantity = long("quantity").default( Long.MAX_VALUE )
    val cotime = integer("cotime").default(0)
    val title = varchar("title", 128)
    val description = varchar("description", 1024)

    override val primaryKey = PrimaryKey(id)
}
