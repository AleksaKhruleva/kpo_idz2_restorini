package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Database Model Table: ORDER
 */
object OrderTable : Table("ORDER") {
    val id = integer("id").autoIncrement()
    val visitorId = integer("visitor_id") references VisitorTable.id
    val price = integer("price").default(0)
    val paid = integer("paid").default(0)
    val cotime = integer("cotime").default(0)
    val timele = integer("timele").default(0)
    val created = datetime("created").clientDefault { LocalDateTime.now() }
    val started = datetime("started").nullable()
    val finished = datetime("finished").nullable()
    val payed = datetime("payed").nullable()

    override val primaryKey = PrimaryKey(id)
}
