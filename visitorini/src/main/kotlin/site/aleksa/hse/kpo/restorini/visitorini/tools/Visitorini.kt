package site.aleksa.hse.kpo.restorini.visitorini.tools

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import site.aleksa.hse.kpo.restorini.common.contract.*
import site.aleksa.hse.kpo.restorini.common.item.MenuItem
import site.aleksa.hse.kpo.restorini.common.item.ReviewItem
import site.aleksa.hse.kpo.restorini.common.util.CommonUtils
import site.aleksa.hse.kpo.restorini.common.util.HashUtils
import kotlin.system.exitProcess

/**
 * Visitorini object
 */
object Visitorini {
    private var visitorId: Int = -1
    private var orderId: Int = -1
    private var paidOrderId: Int = -1
    private var menuItems: MutableList<MenuItem> = mutableListOf()

    private val json = Json {
        encodeDefaults = true
    }

    /**
     * Run Visitor main screen
      */
    fun run() {
        if (visitorId < 1) exitProcess(0)
        while (true) {
            CommonUtils.printSeparator()
            println("1. Display Menu")
            println("2. Create order")
            println("3. Show order")
            println("4. Expand order")
            println("5. Cancel order")
            println("6. Pay order")
            println("q. Quit")
            print("Make your choice: ")
            when (readln().uppercase()) {
                "1" -> displayMenu()
                "2" -> createOrder()
                "3" -> showOrder()
                "4" -> expandOrder()
                "5" -> cancelOrder()
                "6" -> {
                    payOrder()
                    reviewOrder()
                }

                "Q" -> break
                else -> println("Wrong choice :(")
            }
        }
    }

    /**
     * Perform Visitor registration
      */
    fun performSignup() {
        try {
            CommonUtils.printSeparator()
            println("Sign up in progress...")
            println("\tPress Enter for break.")
            print("Input email address: ")
            val email = readln()
            if (email.isEmpty() || email.isBlank()) return
            print("Input password: ")
            val pass1 = readln()
            if (pass1.isEmpty() || pass1.isBlank()) return
            print("Repeat password: ")
            val pass2 = readln()
            if (pass2.isEmpty() || pass2.isBlank()) return
            if (pass1 != pass2) {
                CommonUtils.printSeparator()
                println("\tPasswords do not match!!!")
                return
            }
            CommonUtils.printSeparator()
            val request = SignupRequestContract()
            request.email = email
            request.password = HashUtils.generateHash(pass1)
            VisitoriniSocket.sendString(json.encodeToString(request))
            val jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<SignupResponseContract>(jsonString)
            if (response.completionCode == 0) {
                CommonUtils.printSeparator()
                println("\tSign up successful.")
                visitorId = response.visitorId
            } else {
                CommonUtils.printSeparator()
                when (response.completionCode) {
                    1 -> throw Exception("Email already exists.")
                    else -> throw Exception("Unknown error.")
                }
            }
        } catch (ex: Exception) {
            CommonUtils.printSeparator()
            println("\tSign up error!")
            println(ex.message)
        }
    }

    /**
     * Perform Visitor login
      */
    fun performSignin() {
        try {
            CommonUtils.printSeparator()
            println("Sign in in progress...")
            println("\tPress Enter for break.")
            print("Input email address: ")
            val email = readln()
            if (email.isEmpty() || email.isBlank()) return
            print("Input password: ")
            val pass1 = readln()
            if (pass1.isEmpty() || pass1.isBlank()) return
            val request = SigninRequestContract()
            request.email = email
            request.password = HashUtils.generateHash(pass1)
            VisitoriniSocket.sendString(json.encodeToString(request))
            val jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<SigninResponseContract>(jsonString)
            if (response.completionCode == 0) {
                CommonUtils.printSeparator()
                visitorId = response.visitorId
                orderId = response.orderId
                println("\tSign in successful. Visitor id=$visitorId")
                if (orderId > 0) {
                    println("\tThere is an active order, id=$orderId")
                    showOrder()
                }
            } else {
                CommonUtils.printSeparator()
                println("\tSign in error!")
                when (response.completionCode) {
                    1 -> println("\tEmail or password is incorrect.")
                    else -> println("\tUnknown error.")
                }
            }
        } catch (ex: Exception) {
            CommonUtils.printSeparator()
            println("\tSign in error!")
            println(ex.message)
        }
    }

    /**
     * Display Menu items
     */
    private fun displayMenu() {
        try {
            CommonUtils.printSeparator()
            menuItems.clear()
            val request = MenuRequestContract()
            var jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = Json.decodeFromString<MenuResponseContract>(jsonString)
            if (response.completionCode != 0) throw Exception()
            menuItems += response.menuItems
            menuItems.forEach {
                var quan = "bazillion"
                if (it.quantity < 1) quan = "missing"
                else if (it.quantity < 10) quan = "very few"
                else if (it.quantity < 100) quan = "few"
                else if (it.quantity < 1000) quan = "many"
                println("\tid=${it.id};'${it.title}';₽${it.price};${it.cotime}s;${it.description};#$quan")
            }
        } catch (ex: Exception) {
            println("\tDisplay Menu error.")
            println(ex.message)
        }
    }

    /**
     * Create new Order
     */
    private fun createOrder() {
        try {
            if (orderId > 0) {
                CommonUtils.printSeparator()
                println("Active order already exists.")
                return
            }
            displayMenu()
            CommonUtils.printSeparator()
            println("New order is in progress...")
            println("Press Enter for break.")
            println("Available commands:")
            println("\t+[id] - append menu item id to order")
            println("\t-[id] - remove menu item id from order order")
            println("\t*     - list order items")
            println("Example: +15 - append to order menu item where id = 15")
            println("         -23 - remove from order menu item where id = 23")
            println("         *   - list order items")
            val menuItemIds = mutableListOf<Int>()
            while (true) {
                print("Input command: ")
                val sss = readln()
                if (sss.trim().isEmpty()) break
                when (sss[0]) {
                    '*' -> {
                        menuItemIds.forEach { orderItem: Int ->
                            val menuItem = menuItems.find { menuItem -> menuItem.id == orderItem }
                            if (menuItem == null) throw Exception("New order list contains undefined menu items. id=$orderItem")
                            println("\tid=${menuItem.id};'${menuItem.title}'")
                        }
                    }

                    '-' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong menu item id number.")
                            continue
                        }
                        val orderItem = menuItemIds.find { it == i }
                        if (orderItem == null) {
                            println("\tMenu item id=$i does not exist in the list of selected items.")
                            continue
                        }
                        menuItemIds -= i
                    }

                    '+' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong menu item id number.")
                            continue
                        }
                        val menuItem = menuItems.find { it.id == i }
                        if (menuItem == null) {
                            println("\tMenu item id=$i does not exist.")
                            continue
                        }
                        menuItemIds += i
                    }

                    else -> {
                        println("\tWrong operation character.")
                        continue
                    }
                }
            }
            if (menuItemIds.isEmpty()) {
                println("\tYour order is empty.")
                return
            }
            menuItemIds.sort()
            println("Your order items:")
            menuItemIds.forEach { orderItem: Int ->
                val menuItem = menuItems.find { menuItem -> menuItem.id == orderItem }
                if (menuItem == null) throw Exception("New order list contains undefined menu items. id=$orderItem")
                println("\tid=${menuItem.id};'${menuItem.title}'")
            }
            print("Do you agree with the order? (Y/n): ")
            var jsonString = readln()
            if (jsonString.trim().uppercase() == "N") return
            CommonUtils.printSeparator()
            val request = OrderCreateRequestContract(visitorId, menuItemIds)
            jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<OrderCreateResponseContract>(jsonString)
            when (response.completionCode) {
                0 -> println("\tThe order created. id=${response.orderId}")
                1 -> println("\tThe active order exists. id=${response.orderId}")
                2 -> println("\tThe menu item id=${response.menuItemId} now is not available")
                else -> throw Exception("Unknown error.")
            }
            when (response.completionCode) {
                0 -> orderId = response.orderId
            }
        } catch (ex: Exception) {
            println("\tCreate new order error.")
            println(ex.message)
        }
    }

    /**
     * Show existing Order
     */
    private fun showOrder() {
        try {
            if (orderId < 1) {
                CommonUtils.printSeparator()
                println("Active order does not exist.")
                return
            }
            CommonUtils.printSeparator()
            val request = OrderShowRequestContract(visitorId)
            var jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<OrderShowResponseContract>(jsonString)
            when (response.completionCode) {
                0 -> {
                    println("Order information:")
                    print("\tstatus: ")
                    if (response.finished.isNotEmpty()) println("ready at [${response.finished}]")
                    else if (response.started.isNotEmpty()) println("started at [${response.started}] time left [${response.timele}]")
                    else println("created at [${response.created}]")
                    response.menuItems.forEach {
                        println("\t\tid=${it.id};'${it.title}'")
                    }
                }

                1 -> println("\tNo active order found.")
                else -> throw Exception("Unknown error.")
            }
        } catch (ex: Exception) {
            println("\tShow active order error.")
            println(ex.message)
        }

    }

    /**
     * Add menu items to existing Order
     */
    private fun expandOrder() {
        try {
            if (orderId < 1) {
                CommonUtils.printSeparator()
                println("Active order does not exist.")
                return
            }
            if (menuItems.isEmpty()) {
                displayMenu()
            }
            CommonUtils.printSeparator()
            println("Expand order is in progress...")
            println("Press Enter for break.")
            println("Available commands:")
            println("\t+[id] - append menu item id to order")
            println("\t-[id] - remove menu item id from order order")
            println("\t*     - list order items")
            println("Example: +15 - append to order menu item where id = 15")
            println("         -23 - remove from order menu item where id = 23")
            println("         *   - list order items")
            val menuItemIds = mutableListOf<Int>()
            while (true) {
                print("Input menu item: ")
                val sss = readln()
                if (sss.trim().isEmpty()) break
                when (sss[0]) {
                    '*' -> {
                        menuItemIds.forEach { orderItem: Int ->
                            val menuItem = menuItems.find { menuItem -> menuItem.id == orderItem }
                            if (menuItem == null) throw Exception("New order list contains undefined menu items. id=$orderItem")
                            println("\tid=${menuItem.id};'${menuItem.title}'")
                        }
                    }

                    '-' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong menu item id number.")
                            continue
                        }
                        val orderItem = menuItemIds.find { it == i }
                        if (orderItem == null) {
                            println("\tMenu item id=$i does not exist in the list of selected items.")
                            continue
                        }
                        menuItemIds -= i
                    }

                    '+' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong menu item id number.")
                            continue
                        }
                        val menuItem = menuItems.find { it.id == i }
                        if (menuItem == null) {
                            println("\tMenu item id=$i does not exist.")
                            continue
                        }
                        menuItemIds += i
                    }

                    else -> {
                        println("\tWrong operation character.")
                        continue
                    }
                }
            }
            if (menuItemIds.isEmpty()) {
                println("\tYour order is empty.")
                return
            }
            menuItemIds.sort()
            println("Your order items:")
            menuItemIds.forEach { orderItem: Int ->
                val menuItem = menuItems.find { menuItem -> menuItem.id == orderItem }
                if (menuItem == null) throw Exception("New order list contains undefined menu items. id=$orderItem")
                println("\tid=${menuItem.id};'${menuItem.title}'")
            }
            print("Do you agree with the order? (Y/n): ")
            var jsonString = readln()
            if (jsonString.trim().uppercase() == "N") return
            CommonUtils.printSeparator()
            val request = OrderExpandRequestContract(orderId, menuItemIds)
            jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<OrderExpandResponseContract>(jsonString)
            when (response.completionCode) {
                0 -> println("\tThe order expanded. id=${response.orderId}")
                1 -> println("\tThe order is not expandable. id=${response.orderId}")
                else -> throw Exception("Unknown error.")
            }
            when (response.completionCode) {
                0 -> orderId = response.orderId
            }
        } catch (ex: Exception) {
            println("\tExpand order error.")
            println(ex.message)
        }
    }

    /**
     * Cancel current Order
     */
    private fun cancelOrder() {
        try {
            if (orderId < 1) {
                CommonUtils.printSeparator()
                println("Active order does not exist.")
                return
            }
            CommonUtils.printSeparator()
            val request = OrderCancelRequestContract(orderId)
            var jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<OrderCancelResponseContract>(jsonString)
            when (response.completionCode) {
                0 -> {
                    println("\tOrder canceled.")
                    orderId = -1
                }

                1 -> println("\tNo cancelable order found.")
                else -> throw Exception("Unknown error.")
            }
        } catch (ex: Exception) {
            println("\tCancel order error.")
            println(ex.message)
        }

    }

    /**
     * Pay current Order
     */
    private fun payOrder() {
        paidOrderId = -1
        try {
            if (orderId < 1) {
                CommonUtils.printSeparator()
                println("Active order does not exist.")
                return
            }
            CommonUtils.printSeparator()
            val request = OrderPayRequestContract(orderId)
            var jsonString = json.encodeToString(request)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response = json.decodeFromString<OrderPayResponseContract>(jsonString)
            when (response.completionCode) {
                0 -> {
                    println("\tOrder payed.")
                    paidOrderId = orderId
                    orderId = -1
                }

                1 -> println("\tNo payable order found.")
                else -> throw Exception("Unknown error.")
            }
        } catch (ex: Exception) {
            println("\tPay order error.")
            println(ex.message)
        }
    }

    /**
     * Review current Order
     */
    private fun reviewOrder() {
        try {
            CommonUtils.printSeparator()
            if (paidOrderId < 1) {
                println("\tNo reviewable order available.")
                return
            }
            println("Review order is in progress...")
            val request1 = OrderRequestContract(paidOrderId)
            var jsonString = json.encodeToString(request1)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response1 = json.decodeFromString<OrderResponseContract>(jsonString)
            val orderItems = mutableListOf<MenuItem>()
            if (response1.completionCode == 0) {
                response1.menuItems.forEach { menuItem ->
                    if (orderItems.find { it.id == menuItem.id } == null) {
                        orderItems += MenuItem(menuItem.id, "", "", " ",0, 0, 0, menuItem.title, "")
                    }
                }
                orderItems.forEach {
                    println("\tid=${it.id};'${it.title}'")
                }
            } else {
                throw Exception("Extract order details error.")
            }
            println("Press Enter for break.")
            println("Available commands:")
            println("\t+[id] - append review for menu item id")
            println("\t-[id] - remove review for menu item id")
            println("\t*     - list order items")
            println("Select menu item for review or review item for delete.")
            println("Example: +15 - append review for menu item where id = 15")
            println("         -23 - remove review for menu item where id = 23")
            println("         *  - list review items")
            val reviewItems = mutableListOf<ReviewItem>()
            while (true) {
                print("Input command: ")
                var sss = readln()
                if (sss.trim().isEmpty()) break
                when (sss[0]) {
                    '*' -> {
                        reviewItems.forEach {
                            if (it.id > 0) {
                                println("\treviewId=${it.id};menuItemId=${it.menuItemId};rating=${it.rating};comment='${it.comment}'")
                            }
                        }
                    }

                    '-' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong review item id number.")
                            continue
                        }
                        val reviewItem = reviewItems.find { it.menuItemId == i }
                        if (reviewItem == null) {
                            println("\tReview item id=$i does not exist.")
                            continue
                        }
                        reviewItems -= reviewItem
                    }

                    '+' -> {
                        val i = sss.substring(1).toIntOrNull()
                        if (i == null) {
                            println("\tWrong menu item id number.")
                            continue
                        }
                        val menuItem = orderItems.find { it.id == i }
                        if (menuItem == null) {
                            println("\tMenu item id=$i does not exist.")
                            continue
                        }
                        if (reviewItems.find { it.menuItemId == i } != null) {
                            println("\tThis menu item already reviewed.")
                            continue
                        }
                        print("Input rating (1-5): ")
                        sss = readln().trim()
                        if (sss.isEmpty()) break
                        val rating = sss.toIntOrNull()
                        if (rating == null || rating < 1 || rating > 5) {
                            println("\tWrong rating value.")
                            continue
                        }
                        print("Input comment: ")
                        val comment = readln().trim()
                        if (comment.isEmpty()) break
                        reviewItems += ReviewItem(
                            reviewItems.count() + 1,
                            response1.orderId,
                            menuItem.id,
                            rating,
                            comment
                        )
                    }

                    else -> {
                        println("\tWrong operation character.")
                        continue
                    }
                }
            }
            if (reviewItems.isEmpty()) {
                println("Your review is empty.")
                return
            }
            println("Your review items:")
            reviewItems.forEach {
                if (it.id > 0) {
                    println("\treviewId=${it.id};menuItemId=${it.menuItemId};rating=${it.rating};comment='${it.comment}'")
                }
            }
            print("Do you agree with the review? (Y/n): ")
            if (readln().trim().uppercase() == "N") return
            val request2 = ReviewRequestContract()
            reviewItems.forEach {
                if (it.id > 0) {
                    request2.reviewItems += it
                }
            }
            CommonUtils.printSeparator()
            jsonString = json.encodeToString(request2)
            VisitoriniSocket.sendString(jsonString)
            jsonString = VisitoriniSocket.recvString()
            val response2 = json.decodeFromString<ReviewResponseContract>(jsonString)
            if (response2.completionCode == 0) {
                println("\tReview order - okay!")
            } else {
                throw Exception()
            }
        } catch (ex: Exception) {
            println("\tReview order error.")
            println(ex.message)
        }
    }
}
