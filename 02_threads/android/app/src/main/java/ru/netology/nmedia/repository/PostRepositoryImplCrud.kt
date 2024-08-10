package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImplCrud : PostRepositoryCrud {

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
        if (likedByMe == false) {
            PostsApi.service.likeById(id).enqueue(object :
                Callback<Post> { // Ставим запрос в очередь и передаем Callbackна случай успеха и не успеха
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw java.lang.RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))
                    }
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
        } else {
            PostsApi.service.dislikeById(id).enqueue(object :
                Callback<Post> { // Ставим запрос в очередь и передаем Callbackна случай успеха и не успеха
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw java.lang.RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))
                    }
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
        }

    }

    override fun save(
        post: Post,
        callback: GetCallbackCrud<Post>
    ) { // Метод отправки поста на сервер
        PostsApi.service.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) { //Ответ был получен
                if (response.isSuccessful) {
                    callback.onSuccess(
                        response.body() ?: throw java.lang.RuntimeException("body is null")
                    )
                } else {
                    callback.onError(RuntimeException("Bad code received"))
                }
            }

            override fun onFailure(call: Call<Post>, e: Throwable) { // В случае ошибки
                callback.onError(e)
            }
        })
    }

    override fun removeById(id: Long, callback: GetCallbackCrud<Unit>) { // Асинхронный
        PostsApi.service.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) { //Ответ был получен
                if (response.isSuccessful) {
                    callback.onSuccess(
                        response.body() ?: throw java.lang.RuntimeException("body is null")
                    )
                } else {
                    callback.onError(RuntimeException("Bad code received"))
                }
            }

            override fun onFailure(call: Call<Unit>, e: Throwable) { // В случае ошибки
                callback.onError(e)
            }
        })
    }
}