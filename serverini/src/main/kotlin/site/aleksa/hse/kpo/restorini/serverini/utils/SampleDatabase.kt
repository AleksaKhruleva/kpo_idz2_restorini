package site.aleksa.hse.kpo.restorini.serverini.utils

import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import site.aleksa.hse.kpo.restorini.common.item.MenuItem
import site.aleksa.hse.kpo.restorini.common.util.HashUtils
import site.aleksa.hse.kpo.restorini.serverini.model.*
import java.time.*
import kotlin.system.exitProcess

object SampleDatabase {
    const val menuJSON: String = "[" +
            " {\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Греческий салат\",\"description\":\"Розовые томаты, Болгарский перец, Маслины, Брынза, Красный лук, Орегано, Микс салатов, Морская соль, Огурцы, Оливковое масло. (250гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Капрезе\",\"description\":\"Розовые томаты, Рассольная моцарелла, Микс салатов, Соус песто, Крем бальзамик (200гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Пастрами салат\",\"description\":\"Дымная мраморная говядина Пастрами, Запеченные перцы, Микс салатов, Медово-Горчичная заправка, Хрустящий лук фри. (210гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"С печеной тыквой и фетой\",\"description\":\"Запечённая тыква, Тыквенный мусс, Микс салатов, Фета, Масло оливковое, Вяленые томаты. (240гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Салат с говяжьим языком и ореховым соусом\",\"description\":\"Язык говяжий, Огурцы, Перец сладкий, Микс салата, Соус ореховый, Соус соевый. (170гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стейк салат\",\"description\":\"Говяжья вырезка (Миньон), Микс салатов, Розовые томаты, заправка Стейк салат, Крем бальзамик. (200гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стейк салат с ростбифом\",\"description\":\"Говядина поясничный отруб, Микс салатов, Томаты, Заправка Стейк-салат, Крем-бальзамик. (190гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Страчателла и томаты\",\"description\":\"Розовые томаты, сыр страчателла, бальзамическая заправка, песто собственного приготовления. (240гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Теплый салат с цыпленком\",\"description\":\"Куриное филе, Лук красный, Томаты, Цукини, Кус кус, Микс салатов, Соус ореховый, Песто, Чесночное масло, Соус Соевый. (300гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Цезарь с креветками\",\"description\":\"Креветки, Пармезан, Салат Айсберг, Салат Романо, Помидоры, Соус цезарь, Чесночное масло, Бонбагет. (220гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"A\",\"quantity\":7777,\"cotime\":11,\"title\":\"Цезарь с курицей\",\"description\":\"Куриная грудка, Пармезан, Салат Айсберг, Салат Романо, Помидоры, Соус цезарь, Чесночное масло, Бонбагет. (240гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"\",\"description\":\"\",\"price\":0}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Батат фри\",\"description\":\"Батат фри, Соус Блю чиз. (250/40гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Брокколи\",\"description\":\"Микс Салатов, Смесь пяти перцев, Морская соль, Оливковое масло. (225гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Картофель по деревенски\",\"description\":\"Картофель дольки, Кетчуп. (300/40гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Картофель фри\",\"description\":\"Большая порция картофеля фри, Кетчуп. (300/40гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Картофель фри с пармезаном\",\"description\":\"Картофель фри, Пармезан, Трюфельное масло, Соус Блю-Чиз. (200/40гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Овощи гриль\",\"description\":\"Помидоры, Грибы, Кабачки, Баклажаны, Лук красный, Перец болгарский, Микс салата, Крем бальзамический. (350гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"B\",\"quantity\":7777,\"cotime\":11,\"title\":\"Рис с овощами\",\"description\":\"Рис, Баклажаны, Цуккини, Шампиньоны, Болгарский перец, Лук красный, Соевый соус, Кунжутное масло, Кинза, Имбирь, Чесночное масло.(250гр)\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Дорадо\",\"description\":\"Свежий, охлажденный Дорадо, Микс салатов, Лайм, Морская соль. (350гр)\",\"price\":450}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Свиная рулька\",\"description\":\"Сочная, в меру жирная свинина на кости с тушеной капустой и медово-горчичным соусом. (1 шт)\",\"price\":450}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Свиной томагавк\",\"description\":\"Свиная корейка, Маринад, Капуста, Помидоры. (360/80 гр)\",\"price\":450}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стейк Бавет (зерно)\",\"description\":\"Альтернативный стейк с ярким аромат, особым мясным вкусом и интересной волокнистой текстурой. Не жирное мясо. (250гр)\",\"price\":600}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стейк Рибай (зерно)\",\"description\":\"Премиальный стейк зернового откорма, из мягкой части спинного отруба подлопаточной части туши, имеющей большое количество жировых прожилок.(350гр)\",\"price\":1500}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стейк Рибай (трава)\",\"description\":\"Премиальный стейк из мягкой спинной части отруба, имеющий большое количество жировых прожилок с ярким мясным вкусом. (300гр)\",\"price\":900}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Стриплойн (зерно)\",\"description\":\"Более плотные волокна, чем Рибай, с ярко выраженным мясным вкусом и полоска сладкого жира вдоль всего стейка. (250гр)\",\"price\":600}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Филе Миньон\",\"description\":\"Нежнейший стейк из вырезки (поясничной мышцы) с отсутствием жира. (250гр)\",\"price\":1200}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"C\",\"quantity\":7777,\"cotime\":11,\"title\":\"Шато Бриан\",\"description\":\"Волокнистый стейк из вырезки (поясничной мышцы) с отсутствием жира. (250гр)\",\"price\":900}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Pabst\",\"description\":\"500ml\",\"price\":400}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Б/А Pabst\",\"description\":\"440ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Нефильтрованное «THE БЫК»\",\"description\":\"500ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Сидр «THE БЫК»\",\"description\":\"500ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Темное «THE БЫК»\",\"description\":\"500ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"D\",\"quantity\":7777,\"cotime\":11,\"title\":\"Ячменный лагер «THE БЫК»\",\"description\":\"500ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Американо\",\"description\":\"150 ml\",\"price\":150}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Гляссе\",\"description\":\"200 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Горячий шоколад\",\"description\":\"80 ml\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Капучино\",\"description\":\"150 ml\",\"price\":200}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Латте\",\"description\":\"200 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Латте персик-чили\",\"description\":\"200 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Раф\",\"description\":\"200 ml\",\"price\":200}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Сырный Раф\",\"description\":\"200 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Тыквенный Раф\",\"description\":\"200 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Фраппе\",\"description\":\"150 ml\",\"price\":200}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Эспрессо\",\"description\":\"30 ml\",\"price\":150}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Какао с маршмелоу\",\"description\":\"250 ml\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Капучино\",\"description\":\"150 ml\",\"price\":250}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Латте\",\"description\":\"200 ml\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Латте персик-чили\",\"description\":\"200 ml\",\"price\":300}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Ассам\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Гречишный\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Жасмин\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Манго & Имбирь & Кафирский лайм\",\"description\":\"600 ml\",\"price\":400}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Мандарин&Чёрная смородина\",\"description\":\"600 ml\",\"price\":400}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Молочный улун\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Облепиха&Груша\",\"description\":\"600 ml\",\"price\":400}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Ромашковый\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Сенча\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Таёжный\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Фруктовый пунш\",\"description\":\"600 ml\",\"price\":350}" +
            ",{\"id\":0,\"fromDate\":\"2023-01-01\",\"type\":\"E\",\"quantity\":7777,\"cotime\":11,\"title\":\"Цейлонский с чабрецом\",\"description\":\"600 ml\",\"price\":350}" +
            "]"
    fun create() {
        try {
            val menuItems =
                Json.decodeFromString<List<MenuItem>>(menuJSON)

            Database.connect("jdbc:h2:file:./restorini", driver = "org.h2.Driver", user = "", password = "")

            transaction {
                SchemaUtils.drop(AdminTable, MenuTable, VisitorTable, OrderTable, DetailTable, ReviewTable)
                SchemaUtils.create(AdminTable, MenuTable, VisitorTable, OrderTable, DetailTable, ReviewTable)
                transaction {
                    exec("create or replace force view REPORT1 as SELECT EXTRACT(YEAR FROM \"CREATED\")  AS \"YYYY\", EXTRACT(MONTH FROM \"CREATED\") AS \"MM\", \"PAID\" FROM \"PUBLIC\".\"ORDER\";")
                }

                val adminId = AdminTable.insert {
                    it[fromDate] = LocalDate.now()
                    it[login] = "q"
                    it[password] = HashUtils.generateHash("q")
                } get AdminTable.id

                menuItems.forEach { item ->
                    if (!item.title.trim().isEmpty()) {
                        val menuItemId = MenuTable.insert { it ->
                            it[fromDate] = LocalDate.parse(item.fromDate)
                            it[fromAdminId] = adminId
                            it[type] = item.type
                            it[price] = item.price
                            it[quantity] = item.quantity
                            it[cotime] = item.cotime
                            it[title] = item.title
                            it[description] = item.description
                        } get MenuTable.id
                        println("Menu item Id = $menuItemId added")
                    }
                }

//        Thread.sleep(10000)
            }
        } catch (ex:Exception) {
            println("Create Sample Database file - fail!")
            println(ex.message)
            exitProcess(0)
        }
    }
}
