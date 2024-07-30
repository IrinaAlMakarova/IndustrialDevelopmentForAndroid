package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.GetCallbackCrud
import ru.netology.nmedia.repository.PostRepositoryCrud
import ru.netology.nmedia.repository.PostRepositoryImplCrud
import ru.netology.nmedia.util.SingleLiveEvent

class PostViewModelCrud(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepositoryCrud = PostRepositoryImplCrud()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>() // Событие о том, что пост добавлен
    val postCreated: LiveData<Unit>
        get() = _postCreated

    // Обработка ошибок
    private val _singleError = SingleLiveEvent<Unit>() // Событие о том, что произошла ошибка
    val singleError: LiveData<Unit>
        get() = _singleError
    // Обработка ошибок

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        repository.getAll(object : GetCallbackCrud<List<Post>> {

            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(exception: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : GetCallbackCrud<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(exception: Throwable) {
                    _singleError.postValue(Unit) // Обработка ошибок
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

    fun likeById(id: Long, likedByMe: Boolean) {
        val old = _data.value?.posts.orEmpty()
        repository.likeById(id, likedByMe, object : GetCallbackCrud<Post> {
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

            override fun onError(exception: Throwable) {
                _singleError.postValue(Unit) // Обработка ошибок
                _data.postValue(_data.value?.copy(posts = old)) // В случае ошибки - отмена лайка: Передаем старый список постов, сохраненный в переменную
            }
        })
    }

    fun removeById(id: Long) {
        // Оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : GetCallbackCrud<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(exception: Throwable) {
                _singleError.postValue(Unit) // Обработка ошибок
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}