package site.aleksa.hse.kpo.restorini.utilini

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

/**
 * Backup Database utility
 */
fun main() {
    System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "OFF")

    try {
        Database.connect("jdbc:h2:file:./restorini", driver = "org.h2.Driver", user = "", password = "")
        transaction {
            exec("BACKUP TO 'restorini.db.zip'")
        }
    } catch (ex: Exception) {
        println(ex.message)
    }
}
