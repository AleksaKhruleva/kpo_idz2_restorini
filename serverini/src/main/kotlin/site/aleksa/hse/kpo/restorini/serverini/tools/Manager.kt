package site.aleksa.hse.kpo.restorini.serverini.tools

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.aleksa.hse.kpo.restorini.common.item.MenuItem
import site.aleksa.hse.kpo.restorini.common.util.CommonUtils
import site.aleksa.hse.kpo.restorini.common.util.HashUtils
import site.aleksa.hse.kpo.restorini.serverini.model.*

/**
 * Manager functions
 */
object Manager {
    private var adminId: Int = 0
    private var adminLogin: String = ""

    /**
     * Login Manager function
     */
    fun login() {
        try {
            CommonUtils.printSeparator()
            print("Login: ")
            val login = readln()
            print("Password: ")
            val password = readln()
            val hash = HashUtils.generateHash(password)
            if (authenticate(login, hash)) {
                run()
            } else {
                CommonUtils.printSeparator()
                println("Login or password is incorrect.")
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Authenticate Manager function
      */
    private fun authenticate(candidate: String, hash: String): Boolean {
        try {
            var aid = 0
            transaction {
                val rows = AdminTable.selectAll()
                    .where { AdminTable.login.eq(candidate) }
                    .andWhere { AdminTable.password.eq(hash) }
                    .andWhere { AdminTable.toDate.isNull() }
                if (rows.count() > 0) {
                    aid = rows.single()[AdminTable.id]
                }
            }
            if (aid == 0) return false
            else {
                adminId = aid
                adminLogin = candidate
                return true
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return false
    }

    /**
     * Run Manager screen function
     */
    private fun run() {
        CommonUtils.printSeparator()
        println("Welcome '$adminLogin' to administrator space!")
        println("Menu loading...")
        Menu.load()
        println("Menu loaded.")
        while (true) {
            CommonUtils.printSeparator()
            println("1. Display menu")
            println("2. Add menu item")
            println("3. Delete menu item")
            println("4. Change quantity")
            println("5. Change price")
            println("6. Change cooking time")
            println("q. Quit")
            print("Make your choice: ")
            when (readln().uppercase()) {
                "1" -> displayMenu()
                "2" -> addMenuItem()
                "3" -> deleteMenuItem()
                "4" -> changeQuantity()
                "5" -> changePrice()
                "6" -> changeCotime()
                "Q" -> return
                else -> println("Wrong choice :(")
            }
        }
    }

    /**
     * Display menu items
     */
    private fun displayMenu() {
        try {
            CommonUtils.printSeparator()
            Menu.getMenuItems().forEach {
                println("id=${it.id};'${it.title}';₽${it.price};${it.cotime}s;#${it.quantity}")
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Add Menu Item
     */
    private fun addMenuItem() {
        try {
            CommonUtils.printSeparator()
            println("Add menu item is in progress...")
            while (true) {
                println("Input information about new menu item.")
                println("Enter for break.")
                var iii: Int?
                //  Title
                print("Title: ")
                var sss: String = readln()
                if (sss.isBlank() or sss.isEmpty()) return
                val title = sss
                //  Description
                print("Description: ")
                sss = readln()
                if (sss.isBlank() or sss.isEmpty()) return
                val description = sss
                //  CookingTime
                print("CookingTime: ")
                sss = readln()
                if (sss.isBlank() or sss.isEmpty()) return
                iii = sss.toIntOrNull()
                if (iii == null || iii < 0) {
                    println("!!! Wrong value.")
                    continue
                }
                val cookingTime = iii
                //  Cost
                print("Cost: ")
                sss = readln()
                if (sss.isBlank() or sss.isEmpty()) return
                iii = sss.toIntOrNull()
                if (iii == null || iii < 0) {
                    println("!!! Wrong value.")
                    continue
                }
                val price = iii
                //  Quantity
                print("Quantity (0 - unlimited): ")
                sss = readln()
                if (sss.isBlank() or sss.isEmpty()) return
                var lll = sss.toLongOrNull()
                if (lll == null || lll < 0) {
                    println("!!! Wrong value.")
                    continue
                }
                if (lll < 1) lll = Long.MAX_VALUE
                val quantity = lll.toLong()
                // MemuItem
                val menuItem = MenuItem(0, "", "", " ", price, quantity, cookingTime, title, description)
                val ccc = Menu.addMenuItem(menuItem, adminId)
                println("Menu items added id: $ccc")
                // break
                break
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Delete Menu Item
     */
    private fun deleteMenuItem() {
        try {
            CommonUtils.printSeparator()
            println("Delete menu item is in progress...")
            println("Enter for break.")
            print("Input menu item id for delete: ")
            val s1 = readln()
            if (s1.isEmpty() || s1.isBlank()) return
            val iid1 = s1.toIntOrNull()
            if (iid1 == null) {
                println("Wrong menu item id number.")
                return
            }
            print("Repeat menu item id for delete: ")
            val s2 = readln()
            if (s2.isEmpty() || s2.isBlank()) return
            val iid2 = s2.toIntOrNull()
            if (iid2 == null) {
                println("Wrong menu item id number.")
                return
            }
            if (iid1 != iid2) {
                println("The entered numbers do not match.")
                return
            } else {
                val ccc = Menu.deleteMenuItem(iid1, adminId)
                println("Menu items updated number: $ccc")
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Change Menu item Quantity
     */
    private fun changeQuantity() {
        try {
            CommonUtils.printSeparator()
            println("Change quantity is in progress...")
            println("Enter for break.")
            print("Input menu item id for change quantity: ")
            val s1 = readln()
            if (s1.isEmpty() || s1.isBlank()) return
            val iid = s1.toIntOrNull()
            if (iid == null) {
                println("Wrong menu item id number.")
                return
            }
            print("Input new quantity (0 - unlimited): ")
            val s2 = readln()
            if (s2.isEmpty() || s2.isBlank()) return
            var value = s2.toLongOrNull()
            if (value == null) {
                println("Wrong quantity value.")
                return
            }
            if (value == 0L) value = Long.MAX_VALUE
            val ccc = Menu.changeQuantity(iid, value)
            println("Menu items updated number: $ccc")
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Change Menu item Price
     */
    private fun changePrice() {
        try {
            CommonUtils.printSeparator()
            println("Change price is in progress...")
            println("Enter for break.")
            print("Input menu item id for change price: ")
            val s1 = readln()
            if (s1.isEmpty() || s1.isBlank()) return
            val iid = s1.toIntOrNull()
            if (iid == null) {
                println("Wrong menu item id number.")
                return
            }
            print("Input new price: ")
            val s2 = readln()
            if (s2.isEmpty() || s2.isBlank()) return
            val value = s2.toIntOrNull()
            if (value == null || value < 0) {
                println("Wrong price value.")
                return
            }
            val ccc = Menu.changePrice(iid, value)
            println("Menu items updated number: $ccc")
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Change Menu item Cooking Time
     */
    private fun changeCotime() {
        try {
            CommonUtils.printSeparator()
            println("Change cooking time is in progress...")
            println("Enter for break.")
            print("Input menu item id for change cooking time: ")
            val s1 = readln()
            if (s1.isEmpty() || s1.isBlank()) return
            val iid = s1.toIntOrNull()
            if (iid == null) {
                println("Wrong menu item id number.")
                return
            }
            print("Input new cooking time: ")
            val s2 = readln()
            if (s2.isEmpty() || s2.isBlank()) return
            val value = s2.toIntOrNull()
            if (value == null || value < 0) {
                println("Wrong cooking time value.")
                return
            }
            val ccc = Menu.changeCotime(iid, value)
            println("Menu items updated number: $ccc")
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }
}
