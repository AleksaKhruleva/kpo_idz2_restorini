package site.aleksa.hse.kpo.restorini.serverini.tools

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.aleksa.hse.kpo.restorini.common.item.MenuItem
import site.aleksa.hse.kpo.restorini.serverini.model.*
import java.time.LocalDate

/**
 * 'Restorini' Menu object
 * Singleton
 */
object Menu {
    /**
     * Menu items access synchronisation object
     */
    private val menuItemsLockObject = Object()

    /**
     * Menu items list
      */
    @Serializable
    private var menuItems: MutableList<MenuItem> = mutableListOf()

    /**
     * Menu items list getter
     */
    fun getMenuItems(): List<MenuItem> {
        load()
        return menuItems
    }

    /**
     * Menu items list setter
     */
    private fun setMenuItems(newList: List<MenuItem>) {
        synchronized(menuItemsLockObject) {
            menuItems.clear()
            menuItems += newList
        }
    }

    /**
     * Menu items loader
     */
    fun load() {
        synchronized(menuItemsLockObject) {
            try {
                val newMenuItems = mutableListOf<MenuItem>()
                transaction {
                    MenuTable.selectAll().where { MenuTable.title.neq("") and MenuTable.toDate.isNull() }
                        .forEach {
                            val menuItem = MenuItem(
                                it[MenuTable.id],
                                it[MenuTable.fromDate].toString(),
                                it[MenuTable.toDate].toString(),
                                it[MenuTable.type],
                                it[MenuTable.price],
                                it[MenuTable.quantity],
                                it[MenuTable.cotime],
                                it[MenuTable.title],
                                it[MenuTable.description]
                            )
                            newMenuItems += menuItem
                        }
                }
                setMenuItems(newMenuItems)
            } catch (ex: Exception) {
                println({}.javaClass.enclosingMethod.name)
                println(ex.message ?: "")
            }
        }
    }

    /**
     * Add menu item
     */
    fun addMenuItem(menuItem: MenuItem, id: Int): Int {
        var newId = 0
        try {
            transaction {
                newId = MenuTable.insert {
                    it[fromAdminId] = id
                    it[price] = menuItem.price
                    it[quantity] = menuItem.quantity
                    it[cotime] = menuItem.cotime
                    it[title] = menuItem.title
                    it[description] = menuItem.description
                } get MenuTable.id
            }
            load()
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return newId
    }

    /**
     * Delete menu item
     */
    fun deleteMenuItem(itemId: Int, adminId: Int): Int {
        var count = 0
        try {
            transaction {
                count = MenuTable.update({ MenuTable.id.eq(itemId) and MenuTable.toDate.isNull() }) {
                    it[toDate] = LocalDate.now()
                    it[toAdminId] = adminId
                }
            }
            load()
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return count
    }

    /**
     * change menu item Quantity
     */
    fun changeQuantity(id: Int, value: Long): Int {
        var count = 0
        try {
            transaction {
                count = MenuTable.update({ MenuTable.id.eq(id) and MenuTable.toDate.isNull() }) {
                    it[quantity] = value
                }
            }
            load()
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return count
    }

    /**
     * Change menu item Price
     */
    fun changePrice(id: Int, value: Int): Int {
        var count = 0
        try {
            transaction {
                count = MenuTable.update({ MenuTable.id.eq(id) and MenuTable.toDate.isNull() }) {
                    it[price] = value
                }
            }
            load()
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return count
    }

    /**
     * Change menu item Cooking Time
     */
    fun changeCotime(id: Int, value: Int): Int {
        var count = 0
        try {
            transaction {
                count = MenuTable.update({ MenuTable.id.eq(id) and MenuTable.toDate.isNull() }) {
                    it[cotime] = value
                }
            }
            load()
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
        return count
    }
}
