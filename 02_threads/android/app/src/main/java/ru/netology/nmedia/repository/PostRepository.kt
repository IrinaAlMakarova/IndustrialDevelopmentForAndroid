package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

//interface PostRepository {
//    fun getAll(): List<Post>
//    fun likeById(id: Long, likedByMe: Boolean)
//    fun save(post: Post)
//    fun removeById(id: Long)
//}

interface PostRepository {
    fun getAll(callback: GetCallback<List<Post>>)
    fun likeById(id: Long, likedByMe: Boolean, callback: GetCallback<Post>)
    fun save(callback: GetCallback<Post>)
    fun removeById(id: Long, callback: GetCallback<Unit>)

}

interface GetCallback<T> {
    fun onSuccess(value: T)
    fun onError(exception: Exception)
}
