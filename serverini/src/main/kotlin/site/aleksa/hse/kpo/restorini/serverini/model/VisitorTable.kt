@file:Suppress("unused")

package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

/**
 * Database Model Table:VISITOR
 */
object VisitorTable : Table("VISITOR") {
    val id = integer("id").autoIncrement()
    val fromDate = date("from_date").clientDefault { LocalDate.now() }
    val toDate = date("to_date").nullable()
    val email = varchar("email", 128)
    val password = varchar("password", 1024)

    override val primaryKey = PrimaryKey(id)
}
