package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

/**
 * Database Model Table: ADMIN
 */
object AdminTable : Table("ADMIN") {
    val id = integer("id").autoIncrement()
    val fromDate = date("from_date").clientDefault { LocalDate.now() }
    val toDate = date("to_date").nullable()
    val login = varchar("login", 128)
    val password = varchar("password", 1024)

    override val primaryKey = PrimaryKey(id)
}
