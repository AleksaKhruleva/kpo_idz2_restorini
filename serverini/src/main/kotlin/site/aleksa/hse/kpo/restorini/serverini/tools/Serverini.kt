package site.aleksa.hse.kpo.restorini.serverini.tools

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.year
import org.jetbrains.exposed.sql.transactions.transaction
import site.aleksa.hse.kpo.restorini.common.contract.*
import site.aleksa.hse.kpo.restorini.common.item.MenuItem
import site.aleksa.hse.kpo.restorini.common.util.CommonUtils
import site.aleksa.hse.kpo.restorini.serverini.model.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.text.DecimalFormat
import java.time.LocalDateTime

/**
 * Serverini functions
 */
object Serverini {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Display base statistics for 'Serverini' instance
     */
    fun statistics() {
        try {
            CommonUtils.printSeparator()
            var activeOrderCount = 0L
            var payedOrderCount = 0L
            var canceled1OrderCount = 0L
            var canceled2OrderCount = 0L
            var totalReviewCount = 0L
            transaction {
                //  select active orders (non-payed)
                activeOrderCount = OrderTable.selectAll().where { OrderTable.payed.isNull() }.count()
                //  select paid orders
                payedOrderCount = OrderTable.selectAll()
                    .where { OrderTable.payed.isNotNull() and OrderTable.payed.year().greater(1) }
                    .count()
                //  select canceled before start orders
                canceled1OrderCount = OrderTable.selectAll()
                    .where {
                        OrderTable.payed.isNotNull() and OrderTable.payed.year().eq(1) and OrderTable.started.year()
                            .eq(1)
                    }
                    .count()
                //  select canceled after start orders
                canceled2OrderCount = OrderTable.selectAll()
                    .where {
                        OrderTable.payed.isNotNull() and OrderTable.payed.year().eq(1) and OrderTable.started.year()
                            .greater(1)
                    }
                    .count()
                //  total review
                totalReviewCount = ReviewTable.selectAll().count()
            }
            //  display
            println("Cooking threads started      = ${ServeriniState.cookingThreadsStarted}")
            println("Cooking threads cooking      = ${ServeriniState.cookingThreadsCooking}")
            println("Active orders                = $activeOrderCount")
            println("Payed orders                 = $payedOrderCount")
            println("Orders canceled before start = $canceled1OrderCount")
            println("Orders canceled after start  = $canceled2OrderCount")
            println("Total reviews                = $totalReviewCount")
            //
            transaction {
                val qqq = Report1View.select(
                    Report1View.yyyy, Report1View.mm, Report1View.paid.sum(), Report1View.paid.count()
                ).where { Report1View.paid.greater(0) }
                    .groupBy(Report1View.yyyy, Report1View.mm)
                qqq.forEach {
                    println(
                        "Reporting period: ${it[Report1View.yyyy]}-${
                            it[Report1View.mm].toString().padStart(2, '0')
                        }"
                    )
                    println(
                        "\tPaid  : ${
                            DecimalFormat("#,###").format(it[Report1View.paid.sum()])
                        } ₽"
                    )
                    println(
                        "\tOrders: ${
                            DecimalFormat("#,###").format(it[Report1View.paid.count()])
                        }"
                    )
                }
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Display Popularity for menu items in descending order
     */
    fun popularity() {
        try {
            CommonUtils.printSeparator()
            //  menu items popularity
            println("Menu items popularity:")
            transaction {
                MenuTable.innerJoin(DetailTable)
                    .select(MenuTable.id, MenuTable.title, MenuTable.id.count())
                    .groupBy(MenuTable.id, MenuTable.title)
                    .orderBy(MenuTable.id.count(), SortOrder.DESC)
                    .forEach {
                        println(
                            "\tcount ${
                                DecimalFormat("#,###").format(it[MenuTable.id.count()]).padStart(6, ' ')
                            } title '${it[MenuTable.title]}'"
                        )
                    }
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Display Rating for menu items in descending order
     */
    fun rating() {
        try {
            CommonUtils.printSeparator()
            //  menu items rating
            println("Menu items rating:")
            transaction {
                MenuTable.innerJoin(ReviewTable)
                    .select(MenuTable.id, MenuTable.title, ReviewTable.rating.avg())
                    .groupBy(MenuTable.id, MenuTable.title)
                    .orderBy(ReviewTable.rating.avg(), SortOrder.DESC)
                    .forEach {
                        println("\trating ${it[ReviewTable.rating.avg()].toString()} title '${it[MenuTable.title]}'")
                    }
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Display last N-th reviews for menu items
     */
    fun lastNreview(n: Int) {
        try {
            CommonUtils.printSeparator()
            //  top 15 rating menu items
            println("Last 15 reviews for all menu items:")
            transaction {
                var currentMenuId = -1
                var reviewCounter = 0
                MenuTable.innerJoin(ReviewTable)
                    .selectAll()
                    .orderBy(MenuTable.id, SortOrder.ASC)
                    .orderBy(ReviewTable.id, SortOrder.DESC)
                    .forEach {
                        if (currentMenuId != it[MenuTable.id]) {
                            currentMenuId = it[MenuTable.id]
                            reviewCounter = 0
                            println(
                                "\tMenu item id=${
                                    it[MenuTable.id].toString().padStart(4, '0')
                                } title='${it[MenuTable.title]}"
                            )
                        }
                        if (reviewCounter < n) {
                            println("\t\trating ${it[ReviewTable.rating]} comment '${it[ReviewTable.comment]}'")
                        }
                        reviewCounter += 1
                    }
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Expand Request
     */
    internal fun handleOrderExpandRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderExpandResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderExpandRequestContract>(jsonString)
            transaction {
                try {
                    response.completionCode = 1
                    response.orderId = request.orderId
                    var count = 0
                    var detailId = -1
                    request.menuItemIds.forEach { itemId ->
                        val qqq =
                            MenuTable.update({ MenuTable.id.eq(itemId) and MenuTable.quantity.greater(0) }) {
                                it[quantity] = MenuTable.select(quantity)
                                    .where { id.eq(itemId) }
                                    .first()[quantity] - 1
                            }
                        if (qqq < 1) {
                            response.completionCode = 2
                            response.menuItemId = itemId
                            throw Exception()
                        }
                        count += DetailTable.insert(
                            OrderTable.select(OrderTable.id, stringLiteral(itemId.toString()))
                                .where { OrderTable.id.eq(request.orderId) and OrderTable.finished.isNull() },
                            listOf(DetailTable.orderId, DetailTable.menuItemId)
                        ) ?: 0
                        if (count == 1) {
                            DetailTable.select(DetailTable.id.max())
                                .where { DetailTable.orderId.eq(request.orderId) }
                                .forEach {
                                    val iii = it[DetailTable.id.max()]
                                    detailId = iii ?: 0
                                }
                        }
                    }
                    var newCotime = 0
                    (DetailTable innerJoin MenuTable)
                        .select(MenuTable.cotime.max())
                        .where { DetailTable.orderId.eq(response.orderId) }
                        .andWhere { DetailTable.id.greater(detailId - 1) }
                        .forEach {
                            val iii = it[MenuTable.cotime.max()]
                            newCotime = iii ?: 0
                        }
                    var oldCotime = 0
                    var oldTimele = 0
                    OrderTable.selectAll().where { OrderTable.id.eq(response.orderId) }.forEach {
                        oldCotime = it[OrderTable.cotime]
                        oldTimele = it[OrderTable.timele]
                    }
                    if (newCotime > oldTimele) {
                        OrderTable.update({ OrderTable.id.eq(response.orderId) }) {
                            it[cotime] = oldCotime + newCotime - oldTimele
                        }
                    }
                    if (count == request.menuItemIds.count()) {
                        response.completionCode = 0
                    } else {
                        rollback()
                    }
                } catch (ex: Exception) {
                    rollback()
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Show Request
     */
    internal fun handleOrderShowRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderShowResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderShowRequestContract>(jsonString)
            transaction {
                response.completionCode = 1
                val q1 = OrderTable.selectAll()
                    .where { OrderTable.visitorId.eq(request.visitorId) }
                    .andWhere { OrderTable.payed.isNull() }
                if (q1.count() > 0) {
                    response.completionCode = 0
                    val r1 = q1.first()
                    response.orderId = r1[OrderTable.id]
                    response.price = r1[OrderTable.price]
                    response.cotime = r1[OrderTable.cotime]
                    response.timele = r1[OrderTable.timele]
                    response.created = r1[OrderTable.created].toString()
                    response.started = r1[OrderTable.started].toString().replace("null", "")
                    response.finished = r1[OrderTable.finished].toString().replace("null", "")
                    response.payed = r1[OrderTable.payed].toString().replace("null", "")
                    val q2 = (DetailTable innerJoin MenuTable)
                        .selectAll()
                        .where { DetailTable.orderId.eq(response.orderId) }
                    if (q2.count() > 0) {
                        q2.forEach {
                            response.menuItems += MenuItem(
                                it[MenuTable.id],
                                it[MenuTable.fromDate].toString(),
                                it[MenuTable.toDate]?.toString(),
                                it[MenuTable.type],
                                it[MenuTable.price],
                                it[MenuTable.quantity],
                                it[MenuTable.cotime],
                                it[MenuTable.title],
                                it[MenuTable.description]
                            )
                        }
                    }
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Create Request
     */
    internal fun handleOrderCreateRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderCreateResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderCreateRequestContract>(jsonString)
            transaction {
                try {
                    val query = OrderTable.selectAll()
                        .where { OrderTable.visitorId.eq(request.visitorId) }
                        .andWhere { OrderTable.finished.isNull() }
                    if (query.count() > 0) {
                        response.completionCode = 1
                        response.orderId = query.first()[OrderTable.id]
                    } else {
                        response.completionCode = 0
                        response.orderId = OrderTable.insert {
                            it[visitorId] = request.visitorId
                        } get OrderTable.id
                        request.menuItemIds.forEach { itemId ->
                            val count =
                                MenuTable.update({ MenuTable.id.eq(itemId) and MenuTable.quantity.greater(0) and MenuTable.toDate.isNull() }) {
                                    it[quantity] = MenuTable.select(quantity)
                                        .where { id.eq(itemId) }
                                        .first()[quantity] - 1
                                }
                            if (count < 1) {
                                response.completionCode = 2
                                response.menuItemId = itemId
                                throw Exception()
                            }
                            DetailTable.insert {
                                it[orderId] = response.orderId
                                it[menuItemId] = itemId
                            }
                        }
                        OrderTable.update({ OrderTable.id.eq(response.orderId) }) {
                            it[price] = (DetailTable innerJoin MenuTable)
                                .select(MenuTable.price.sum())
                                .where { DetailTable.orderId.eq(response.orderId) }
                        }
                        OrderTable.update({ OrderTable.id.eq(response.orderId) }) {
                            it[cotime] = (DetailTable innerJoin MenuTable)
                                .select(MenuTable.cotime.max())
                                .where { DetailTable.orderId.eq(response.orderId) }
                        }
                        OrderTable.update({ OrderTable.id.eq(response.orderId) }) {
                            it[timele] = (DetailTable innerJoin MenuTable)
                                .select(MenuTable.cotime.max())
                                .where { DetailTable.orderId.eq(response.orderId) }
                        }
                    }
                } catch (ex: Exception) {
                    rollback()
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
            if (response.completionCode == 0) {
                Kitchen.cookingThread(response.orderId)
            }
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Signin Request
     */
    internal fun handleSigninRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = SigninResponseContract()
            val request = json.decodeFromString<SigninRequestContract>(
                dataArray.toString(Charsets.UTF_8)
            )
            transaction {
                response.completionCode = 1
                VisitorTable.selectAll()
                    .where { VisitorTable.toDate.isNull() }
                    .andWhere { VisitorTable.email.eq(request.email) }
                    .andWhere { VisitorTable.password.eq(request.password) }
                    .forEach {
                        response.completionCode = 0
                        response.visitorId = it[VisitorTable.id]
                    }
                if (response.completionCode == 0) {
                    OrderTable.selectAll()
                        .where { OrderTable.visitorId.eq(response.visitorId) }
                        .andWhere { OrderTable.payed.isNull() }
                        .forEach {
                            response.orderId = it[OrderTable.id]
                        }
                }
            }
            val jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Signup Request
     */
    internal fun handleSignupRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = SignupResponseContract()
            val request = json.decodeFromString<SignupRequestContract>(
                dataArray.toString(Charsets.UTF_8)
            )
            transaction {
                val ii = VisitorTable.selectAll()
                    .where { VisitorTable.toDate.isNull() }
                    .andWhere { VisitorTable.email.eq(request.email) }
                    .count()
                if (ii < 1) {
                    response.completionCode = 0
                    response.visitorId = VisitorTable.insert {
                        it[email] = request.email
                        it[password] = request.password
                    } get VisitorTable.id
                } else {
                    response.completionCode = 1
                }
            }
            val jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Menu Request
     */
    internal fun handleMenuRequest(serverSocket: DatagramSocket, datagramPacket: DatagramPacket) {
        try {
            val response = MenuResponseContract()
            response.completionCode = 0
            response.menuItems += Menu.getMenuItems()
            val jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Pay Request
     */
    internal fun handleOrderPayRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderPayResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderPayRequestContract>(jsonString)
            transaction {
                response.orderId = request.orderId
                response.completionCode = 1
                var count =
                    OrderTable.update({ OrderTable.id.eq(request.orderId) and OrderTable.finished.isNotNull() and OrderTable.payed.isNull() }) {
                        it[payed] = LocalDateTime.now()
                        it[paid] = (OrderTable.select(price).where { id.eq(request.orderId) })
                    }
                if (count > 0) {
                    response.completionCode = 0
                } else {
                    count = OrderTable.selectAll()
                        .where { OrderTable.id.eq(request.orderId) }
                        .andWhere { OrderTable.finished.isNotNull() }
                        .andWhere { OrderTable.payed.isNotNull() }
                        .count().toInt()
                    if (count > 0) {
                        response.completionCode = 2
                    }
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Cancel Request
     */
    internal fun handleOrderCancelRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderCancelResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderCancelRequestContract>(jsonString)
            transaction {
                response.completionCode = 1
                var count = 0
                count += OrderTable.update({ OrderTable.id.eq(request.orderId) and OrderTable.finished.isNull() }) {
                    it[finished] = LocalDateTime.now()
                    it[payed] = LocalDateTime.parse("0001-01-01T00:00:00")
                }
                if (count > 0) {
                    response.completionCode = 0
                    OrderTable.update({ OrderTable.id.eq(request.orderId) and OrderTable.started.isNull() }) {
                        it[started] = LocalDateTime.parse("0001-01-01T00:00:00")
                    }
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Review Request
     */
    internal fun handleReviewRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = ReviewResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<ReviewRequestContract>(jsonString)
            transaction {
                request.reviewItems.forEach { reviewItem ->
                    ReviewTable.insert {
                        it[orderId] = reviewItem.orderId
                        it[menuItemId] = reviewItem.menuItemId
                        it[rating] = reviewItem.rating
                        it[comment] = reviewItem.comment
                    }
                }
            }
            response.completionCode = 0
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }

    /**
     * Handle Order Request
     */
    internal fun handleOrderRequest(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        try {
            val response = OrderResponseContract()
            var jsonString = dataArray.toString(Charsets.UTF_8)
            val request = json.decodeFromString<OrderRequestContract>(jsonString)
            transaction {
                response.completionCode = 1
                val q1 = OrderTable.selectAll()
                    .where { OrderTable.id.eq(request.orderId) }
                if (q1.count() > 0) {
                    response.completionCode = 0
                    val r1 = q1.first()
                    response.orderId = r1[OrderTable.id]
                    response.price = r1[OrderTable.price]
                    response.cotime = r1[OrderTable.cotime]
                    response.timele = r1[OrderTable.timele]
                    response.created = r1[OrderTable.created].toString()
                    response.started = r1[OrderTable.started].toString().replace("null", "")
                    response.finished = r1[OrderTable.finished].toString().replace("null", "")
                    response.payed = r1[OrderTable.payed].toString().replace("null", "")
                    val q2 = (DetailTable innerJoin MenuTable)
                        .selectAll()
                        .where { DetailTable.orderId.eq(response.orderId) }
                    if (q2.count() > 0) {
                        q2.forEach {
                            response.menuItems += MenuItem(
                                it[MenuTable.id],
                                it[MenuTable.fromDate].toString(),
                                it[MenuTable.toDate]?.toString(),
                                it[MenuTable.type],
                                it[MenuTable.price],
                                it[MenuTable.quantity],
                                it[MenuTable.cotime],
                                it[MenuTable.title],
                                it[MenuTable.description]
                            )
                        }
                    }
                }
            }
            jsonString = json.encodeToString(response)
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            serverSocket.send(DatagramPacket(jsonBytes, jsonBytes.size, datagramPacket.address, datagramPacket.port))
        } catch (ex: Exception) {
            println({}.javaClass.enclosingMethod.name)
            println(ex.message ?: "")
        }
    }
}
