package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    //override fun getAll(): List<Post> { // Синхронный
    //    val request: Request = Request.Builder()
    //        .url("${BASE_URL}/api/slow/posts")
    //        .build()

    //    return client.newCall(request)
    //        .execute()
    //        .let { it.body?.string() ?: throw RuntimeException("body is null") }
    //        .let {
    //            gson.fromJson(it, typeToken.type)
    //        }
    //}
    override fun getAll(callback: GetCallback<List<Post>>) { // Асинхронный
        // Формируем запрос на список постов
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object :
                Callback { // Ставим запрос в очередь и передаем Callbackна случай успеха и не успеха
                override fun onResponse(call: Call, response: Response) {
                    val posts: List<Post> =
                        response.let { it.body?.string() ?: throw RuntimeException("body is null") }
                            .let {
                                gson.fromJson(it, typeToken.type)
                            }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    //override fun likeById(id: Long, likedByMe: Boolean) { // Синхронный
    //    if (likedByMe == true) {
    //        // Формируем запрос к серверу на добавление лайка
    //        val request: Request = Request.Builder() //Добавление лайка: POST /api/posts/{id}/likes
    //            .url("${BASE_URL}/api/slow/posts/$id/likes")
    //            .build()

    // Отправка запроса
    //        client.newCall(request)
    //            .execute()
    //            .close()
    //    } else {
    // Формируем запрос к серверу на удаление лайка
    //        val request: Request = Request.Builder() //Удаление лайка: DELETE /api/posts/{id}/likes
    //            .delete()
    //            .url("${BASE_URL}/api/slow/posts/$id/likes")
    //            .build()

    // Отправка запроса
    //        client.newCall(request)
    //            .execute()
    //            .close()
    //    }
    //}
    override fun likeById(id: Long, likedByMe: Boolean, callback: GetCallback<Post>) {// Асинхронный
        if (likedByMe == true) {
            // Формируем запрос к серверу на добавление лайка
            val request: Request = Request.Builder() //Добавление лайка: POST /api/posts/{id}/likes
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .delete()
                .build()

            // Отправка запроса
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val post: Post = response
                            .let { it.body?.string() ?: throw RuntimeException("body is null") }
                            .let { gson.fromJson(it, Post::class.java) }
                        callback.onSuccess(post)
                    }

                    override fun onFailure(call: Call, e: java.io.IOException) {
                        callback.onError(e)
                    }
                })
        } else {
            // Формируем запрос к серверу на удаление лайка
            val request: Request = Request.Builder() //Удаление лайка: DELETE /api/posts/{id}/likes
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .post(gson.toJson(callback).toRequestBody(jsonType))
                .build()

            // Отправка запроса
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val post: Post = response
                            .let { it.body?.string() ?: throw RuntimeException("body is null") }
                            .let { gson.fromJson(it, Post::class.java) }
                        callback.onSuccess(post)
                    }

                    override fun onFailure(call: Call, e: java.io.IOException) {
                        callback.onError(e)
                    }
                })
        }
    }


    //override fun save(post: Post) {
    //    val request: Request = Request.Builder()
    //        .post(gson.toJson(post).toRequestBody(jsonType))
    //        .url("${BASE_URL}/api/slow/posts")
    //        .build()

    //    client.newCall(request)
    //        .execute()
    //        .close()
    //}
    override fun save(post: Post, callback: GetCallback<Post>) { // Метод отправки поста на сервер
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) { //Ответ был получен
                    val post: Post = response
                        .let { it.body?.string() ?: throw RuntimeException("body is null") }
                        .let { gson.fromJson(it, Post::class.java) }
                    callback.onSuccess(post)
                }

                override fun onFailure(call: Call, e: java.io.IOException) { // В случае ошибки
                    callback.onError(e)
                }
            })
    }

    //override fun removeById(id: Long) { // Синхронный
    //    val request: Request = Request.Builder()
    //        .delete()
    //        .url("${BASE_URL}/api/slow/posts/$id")
    //        .build()

    //    client.newCall(request)
    //        .execute()
    //        .close()
    //}
    override fun removeById(id: Long, callback: GetCallback<Unit>) { // Асинхронный
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback { // Ставим запрос в очередь
                override fun onResponse(call: Call, response: Response) { //Ответ был получен
                    val post: Unit = response.close()
                    callback.onSuccess(post)
                }

                override fun onFailure(call: Call, e: java.io.IOException) { // В случае ошибки
                    callback.onError(e)
                }
            })
    }
}
