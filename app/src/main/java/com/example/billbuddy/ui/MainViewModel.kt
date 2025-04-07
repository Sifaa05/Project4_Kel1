package com.example.billbuddy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant

class MainViewModel : ViewModel() {
    private val repository = SplitBillRepository()

    private val _eventData = MutableLiveData<EventData?>()
    val eventData: LiveData<EventData?> get() = _eventData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun createEvent(
        creatorId: String,
        creatorName: String,
        eventName: String,
        items: List<Item>,
        participants: List<Participant>,
        splitType: String
    ) {
        repository.createSplitEvent(
            creatorId = creatorId,
            creatorName = creatorName,
            eventName = eventName,
            items = items,
            participants = participants,
            splitType = splitType,
            onSuccess = { eventId ->
                getEventDetails(eventId) // Ambil detail event setelah dibuat
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun getEventDetails(eventId: String) {
        repository.getEventDetails(
            eventId = eventId,
            onSuccess = { data ->
                _eventData.value = data
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun updatePaymentStatus(eventId: String, participantId: String, paid: Boolean) {
        repository.updatePaymentStatus(
            eventId = eventId,
            participantId = participantId,
            paid = paid,
            onSuccess = {
                getEventDetails(eventId) // Perbarui data setelah status berubah
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }
}