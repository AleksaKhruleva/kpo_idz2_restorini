package site.aleksa.hse.kpo.restorini.serverini.model

import org.jetbrains.exposed.sql.Table

/**
 * Database Model View: REPORT1
 */
object Report1View :Table("REPORT1") {
    val yyyy = integer("yyyy")
    val mm = integer("mm")
    val paid = integer("paid")
}