package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImplCrud : PostRepositoryCrud {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: GetCallbackCrud<List<Post>>) { // Асинхронный
        PostsApi.service.getAll().enqueue(object :
            Callback<List<Post>> { // Ставим запрос в очередь и передаем Callbackна случай успеха и не успеха
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    callback.onSuccess(
                        response.body() ?: throw java.lang.RuntimeException("body is null")
                    )
                } else {
                    callback.onError(RuntimeException("Bad code received"))
                }
            }

            override fun onFailure(call: Call<List<Post>>, e: Throwable) {
                callback.onError(e)
            }
        })
    }

    override fun likeById(
        id: Long,
        likedByMe: Boolean,
        callback: GetCallbackCrud<Post>
    ) {// Асинхронный
    }

    override fun save(
        post: Post,
        callback: GetCallbackCrud<Post>
    ) { // Метод отправки поста на сервер
        //    PostsApi.service.save().enqueue(object : Callback<Post> {
        //        override fun onResponse(call: Call<Post>, response: Response<Post>) { //Ответ был получен
        //            if (response.isSuccessful) {
        //                callback.onSuccess(
        //                    response.body() ?: throw java.lang.RuntimeException("body is null")
        //                )
        //            }else{
        //                callback.onError(RuntimeException("Bad code received"))
        //            }
        //        }

        //        override fun onFailure(call: Call<Post>, e: Throwable) { // В случае ошибки
        //            callback.onError(e)
        //        }
        //    })
    }

    override fun removeById(id: Long, callback: GetCallbackCrud<Unit>) { // Асинхронный
    }
}
