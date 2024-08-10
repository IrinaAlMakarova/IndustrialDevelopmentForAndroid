## Coroutines в Android

### Задача №1. Remove & likes
Легенда
Используя код и сервер из лекции, реализуйте в проекте функциональность удаления и проставления лайков. Для этого нужно отредактировать PostViewModel и PostRepositoryImpl:

// PostViewModel
fun likeById(id: Long) {
    TODO()
}

fun removeById(id: Long) {
    TODO()
}

// PostRepositoryImpl
override suspend fun removeById(id: Long) {
    TODO("Not yet implemented")
}

override suspend fun likeById(id: Long) {
    TODO("Not yet implemented")
}
Логика работы:

Сначала модифицируете запись в локальной БД или удаляете.
Затем отправляете соответствующий запрос в API (HTTP).
Не забудьте об обработке ошибок и кнопке Retry в случае, если запрос в API завершился с ошибкой (в том числе в случае отсутствия сетевого соединения*).

Примечание*: не обязательно перезапускать сервер. Достаточно отключить сеть в шторке телефона/эмулятора.
