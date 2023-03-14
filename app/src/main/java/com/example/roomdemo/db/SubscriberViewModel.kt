package com.example.roomdemo.db

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdemo.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {

        if(inputName.value == null) {
            statusMessage.value = Event("Error : Please enter subscriber's name.")
        }else if(inputEmail.value == null) {
            statusMessage.value = Event("Error : Please enter subscriber's email.")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Error : Please enter valid email.")
        }else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!

                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))

                inputName.value = ""
                inputEmail.value = ""
            }
        }


    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    fun insert(subscriber: Subscriber) {
        viewModelScope.launch(Dispatchers.IO) {
            val newRowID = repository.insert(subscriber)
            withContext(Dispatchers.Main) {
                if (newRowID > -1) {
                    statusMessage.value = Event("Subscriber Inserted Successfully at ${newRowID}")
                } else {
                    statusMessage.value = Event("Error Occurred while Insert")
                }
            }

        }
    }

    fun update(subscriber: Subscriber) {
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRows = repository.update(subscriber)
            withContext(Dispatchers.Main) {
                if (numberOfRows > 0) {
                    inputName.value = ""
                    inputEmail.value = ""

                    isUpdateOrDelete = false

                    saveOrUpdateButtonText.value = "Save"
                    clearAllOrDeleteButtonText.value = "Clear All"

                    statusMessage.value = Event("${numberOfRows} Row(s) Updated Successfully")
                } else {
                    statusMessage.value = Event("Error occurred while updating subscriber")
                }
            }
        }
    }

    fun delete(subscriber: Subscriber) {
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRowsDeleted = repository.delete(subscriber)
            withContext(Dispatchers.Main) {
                if (numberOfRowsDeleted > 0) {
                    inputName.value = ""
                    inputEmail.value = ""

                    isUpdateOrDelete = false

                    saveOrUpdateButtonText.value = "Save"
                    clearAllOrDeleteButtonText.value = "Clear All"

                    statusMessage.value =
                        Event("${numberOfRowsDeleted} Row(s) Deleted Successfully")
                } else {
                    statusMessage.value = Event("Error occurred while deleting subscriber")
                }
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRowsDeleted = repository.deleteAll()
            withContext(Dispatchers.Main) {
                if (numberOfRowsDeleted > 0) {
                    statusMessage.value =
                        Event("${numberOfRowsDeleted} Row(s) Deleted Successfully")
                } else {
                    statusMessage.value = Event("Error occurred while deleting all subscribers")
                }
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email

        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber

        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }
}