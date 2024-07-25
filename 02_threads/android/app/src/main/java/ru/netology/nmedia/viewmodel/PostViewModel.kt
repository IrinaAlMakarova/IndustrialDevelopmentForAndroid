package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>() // Событие о том, что пост добавлен
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    //fun loadPosts() { // Загрузка постов с сервера
    //    thread {
    //        // Начинаем загрузку
    //        _data.postValue(FeedModel(loading = true))
    //        try {
    //            // Данные успешно получены
    //            val posts = repository.getAll()
    //            FeedModel(posts = posts, empty = posts.isEmpty())
    //        } catch (e: IOException) {
    //            // Получена ошибка
    //            FeedModel(error = true)
    //        }.also(_data::postValue)
    //    }
    //}
    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        repository.getAll(object : GetCallback<List<Post>> {

            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(exception: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    //fun save() {
    //    edited.value?.let {
    //        thread {
    //            repository.save(it)
    //            _postCreated.postValue(Unit)
    //        }
    //    }
    //    edited.value = empty
    //}
    fun save() {
        edited.value?.let {
            repository.save(it, object : GetCallback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(exception: Exception) {
                }
            })
        }
        edited.value = empty
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    //fun likeById(id: Long, likedByMe: Boolean) {
    //    thread { // Создаем фоновый поток
    //        val old = _data.value?.posts.orEmpty()
    //        _data.postValue(
    //            _data.value?.copy(posts = _data.value?.posts.orEmpty()
    //                .map {
    //                    if (it.id != id) it else it.copy(
    //                        likedByMe = !it.likedByMe,
    //                        likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
    //                    )
    //                }
    //            )
    //        )
    //        try {
    //            repository.likeById(id, likedByMe) // Обращение к серверу
    //        } catch (e: IOException) {
    //            _data.postValue(_data.value?.copy(posts = old)) // В случае ошибки - отмена лайка: Передаем старый список постов, сохраненный в переменную
    //        }
    //    }
    //}
    fun likeById(id: Long, likedByMe: Boolean) {
        val old = _data.value?.posts.orEmpty()
        repository.likeById(id, likedByMe, object : GetCallback<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id != id) it else it.copy(
                                likedByMe = !it.likedByMe,
                                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                            )
                        }
                    )
                )
            }

            override fun onError(exception: Exception) {
                _data.postValue(_data.value?.copy(posts = old)) // В случае ошибки - отмена лайка: Передаем старый список постов, сохраненный в переменную
            }
        })
    }

    //fun removeById(id: Long) {
    //    thread {
    //        // Оптимистичная модель
    //        val old = _data.value?.posts.orEmpty()
    //        _data.postValue(
    //            _data.value?.copy(posts = _data.value?.posts.orEmpty()
    //                .filter { it.id != id }
    //            )
    //        )
    //        try {
    //            repository.removeById(id)
    //        } catch (e: IOException) {
    //            _data.postValue(_data.value?.copy(posts = old))
    //        }
    //    }
    //}
    fun removeById(id: Long) {
        // Оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : GetCallback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(exception: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}