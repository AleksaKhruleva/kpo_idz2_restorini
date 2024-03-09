# Программный комплекс управления заказами в ресторане - 'Restorini'

## Технологический стек

- Язык разработки - Kotlin
- СУБД - Embedded H2
- ORM - Jetbrains Exposed DSL
- Коммуникация Клиент-Сервер - UDP

## Значения по умолчаю

- Имя локального файла Базы Данных приложнения - restorini.mv.db
- UDP-порт сервера приложения - 50107

## Шаблоны проектирования

- Singleton Pattern - использутеся для объектов, которые могут существовать только в единственном экземпляре (например, Меню)
- DTO (Data Transfer Object) Pattern - используется для передачи данных между клиентом и сервером в стандартизированном виде, реализован в виде классов контрактов
- Command Pattern - используется при реализации запросов, поступающих на сервер (применяется для инкапсуляции запроса в объект)

## Структура программного комплекса:

- [Приложение Сервер 'Serverini'](#приложение-сервер-serverini) (включает рабочее место администратора)
- [Приложение Клиент 'Visitorini'](#приложение-клиент-visitorini)
- [Набор утилит](#набор-утилит)

## Приложение Сервер 'Serverini'
<pre>
Главный класс приложения:
site.aleksa.hse.kpo.restorini.serverini.ServeriniApp
</pre>

### Основное меню приложения
<pre>
1. Statistics
2. Menu items Popularity
3. Menu items Rating
4. View last 5 reviews for menu items
t. Toggle trace output
q. Quit
0. Administrator space
</pre>

#### 1. Statistics
Выводит текущее состояние севера приложения и базовую статистику.
#### 2. Menu items Popularity
Выводит популярность всех пунктов меню в порядке убывания.
#### 3. Menu items Rating
Выводит рейтинг всех пунктов меню в порядке убывания.
#### 4. View last 5 reviews for menu items
Выводит 5 последних отзывов для каждого пункта меню.
#### t. Toggle trace output
Включает/выключает печать диагностической информации выполнения потоков прграммы.
#### q. Quit
Выход из приложения.
#### 0. Administrator space
Раздел администратора.
<pre>
1. Display menu
2. Add menu item
3. Delete menu item
4. Change quantity
5. Change price
6. Change cooking time
q. Quit
</pre>
##### 1. Display menu
Отобразить действующее меню. Формат выводимой строки:
<pre>
id=[идентификатор];'Наименование_Блюда';₽[Стоимость];[Секунд_приготовления]s;#[Количество]
</pre>
##### 2. Add menu item
Добавить новый пункт меню.
##### 3. Delete menu item
Удалить пункт меню.
##### 4. Change quantity
Изменить оставшееся количество.
##### 5. Change price
Изменить цену.
##### 6. Change cooking time
Изменить время приготовления.
##### q. Quit
Выход из раздела Администратора.

## Приложение клиент 'Visitorini'
<pre>
Главный класс приложения:
site.aleksa.hse.kpo.restorini.visitorini.VisitoriniApp
</pre>

### Основное меню приложения
<pre>
1. Display Menu
2. Create order
3. Show order
4. Expand order
5. Cancel order
6. Pay order
q. Quit
</pre>
#### 1. Display Menu
Отобразить действующее меню. Формат выводимой строки:
<pre>
id=[идентификатор];'Наименование_Блюда';₽[Стоимость];[Секунд_приготовления]s;[Описание];#[Количество]
</pre>
#### 2. Create order
Создание нового заказа.
#### 3. Show order
Печать текущего заказа и его состояния.
#### 4. Expand order
Добавлени одного или нескольких блюд в заказ.
#### 5. Cancel order
Отмена заказа.
#### 6. Pay order
Оплата заказа и оставление отзыва.
#### q. Quit
Выход их приложения.

## Набор утилит

### _ManagerAdd
Создание учётной записи Менеджера (Администратора).
<pre>
Главный класс приложения:
site.aleksa.hse.kpo.restorini.utilini._ManagerAdd
</pre>
Запускается и используется автономно. Запрашивает логин, пароль и подтверждения пароля.

### _ManagerDel
Удаление учётной записи Менеджера.
<pre>
Главный класс приложения:
site.aleksa.hse.kpo.restorini.utilini._ManagerDel
</pre>
Запускается и используется автономно. Запрашивает логин и подтверждени логина.

### _BackupDatabase
Создание резервной копии Базы Данных.
<pre>
Главный класс приложения:
site.aleksa.hse.kpo.restorini.utilini._BackupDatabase
</pre>
Запускается и используется автономно. Создаёт резерную копию с именем 'restorini.db.zip'.
