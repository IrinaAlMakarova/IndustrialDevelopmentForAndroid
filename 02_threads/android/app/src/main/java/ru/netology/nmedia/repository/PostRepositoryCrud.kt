package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepositoryCrud {
    fun getAll(callback: GetCallbackCrud<List<Post>>)
    fun likeById(id: Long, likedByMe: Boolean, callback: GetCallbackCrud<Post>)
    fun save(post: Post, callback: GetCallbackCrud<Post>)
    fun removeById(id: Long, callback: GetCallbackCrud<Unit>)
}

interface GetCallbackCrud<T> {
    fun onSuccess(value: T)
    fun onError(e: Throwable)
}
