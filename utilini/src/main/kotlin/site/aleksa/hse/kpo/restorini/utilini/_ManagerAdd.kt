package site.aleksa.hse.kpo.restorini.utilini

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import site.aleksa.hse.kpo.restorini.common.util.HashUtils
import site.aleksa.hse.kpo.restorini.serverini.model.*
import java.time.*
import kotlin.system.exitProcess

/**
 * Add new Manager login
 */
fun main() {
    System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "OFF")

    try {
        Database.connect("jdbc:h2:file:./restorini", driver = "org.h2.Driver", user = "", password = "")

        println("Add NEW admin in progress...")
        print("Input login: ")
        val adminLogin = readln()
        if (adminLogin.trim().isEmpty()) exitProcess(0)

        var i: Long = 0
        transaction {
            i = AdminTable.selectAll()
                .where { AdminTable.login.eq(adminLogin) }
                .andWhere { AdminTable.toDate.isNull() }
                .count()
        }

        if (i > 0) {
            println("Active admin login '$adminLogin' already exist.")
            exitProcess(0)
        }

        print("Input password: ")
        val pass1 = readln()
        if (pass1.trim().isEmpty()) exitProcess(0)
        print("Repeat password: ")
        val pass2 = readln()
        if (pass2.trim().isEmpty()) exitProcess(0)
        if (pass1 != pass2) {
            println("\tPasswords do not match!!!")
            exitProcess(0)
        }

        transaction {
            val adminId = AdminTable.insert {
                it[fromDate] = LocalDate.now()
                it[login] = adminLogin
                it[password] = HashUtils.generateHash(pass1)
            } get AdminTable.id
            println("New admin login id=$adminId")
        }
    } catch (ex: Exception) {
        println(ex.message)
    }
}
