package com.example.billbuddy.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.data.EventData
import com.example.billbuddy.data.Item
import com.example.billbuddy.data.Participant
import kotlinx.coroutines.launch

//import com.example.billbuddy.model.UserProfile

class MainViewModel : ViewModel() {
    private val repository = SplitBillRepository()

    private val _eventData = MutableLiveData<EventData?>()
    val eventData: LiveData<EventData?> get() = _eventData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _allEvents = MutableLiveData<List<EventData>>()
    val allEvents: LiveData<List<EventData>> get() = _allEvents

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
        viewModelScope.launch {
            repository.updatePaymentStatus(
                eventId = eventId,
                participantId = participantId,
                paid = paid,
                onSuccess = {
                    // Refresh data event setelah status diubah
                    getEventDetails(eventId)
                },
                onFailure = { exception ->
                    _error.postValue(exception.message)
                }
            )
        }
    }

    fun getAllEvents() {
        Log.d("MainViewModel", "Mengambil semua event")
        repository.getAllEvents(
            onSuccess = { events ->
                Log.d("MainViewModel", "Berhasil mengambil ${events.size} event")
                _allEvents.value = events
                _error.value = null
            },
            onFailure = { exception ->
                Log.e("MainViewModel", "Gagal mengambil event: ${exception.message}")
                _error.value = exception.message
            }
        )
    }

    private val _searchResults = MutableLiveData<List<EventData>>(emptyList())
    val searchResults: LiveData<List<EventData>> get() = _searchResults

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        repository.searchEvents(
            query = query,
            onSuccess = { events ->
                _searchResults.value = events
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    private val _activeEvents = MutableLiveData<List<EventData>>(emptyList())
    val activeEvents: LiveData<List<EventData>> get() = _activeEvents
    fun getActiveEvents() {
        Log.d("MainViewModel", "Mengambil event aktif")
        repository.getActiveEvents(
            onSuccess = { events ->
                Log.d("MainViewModel", "Berhasil mengambil ${events.size} event aktif")
                _activeEvents.value = events
                _error.value = null
            },
            onFailure = { exception ->
                Log.e("MainViewModel", "Gagal mengambil event aktif: ${exception.message}")
                _error.value = exception.message
            }
        )
    }

    fun addParticipant(
        eventId: String,
        participantName: String
    ) {
        repository.addParticipant(
            eventId = eventId,
            participantName = participantName,
            onSuccess = {
                getEventDetails(eventId) // Perbarui data event setelah participant ditambahkan
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit
    ) {
        repository.deleteEvent(
            eventId = eventId,
            onSuccess = {
                onSuccess() // Panggil callback onSuccess untuk navigasi
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun addParticipant(
        eventId: String,
        participantName: String,
        itemsAssigned: List<String> = emptyList() // Default ke emptyList jika tidak ada item yang diassign
    ) {
        repository.addParticipant(
            eventId = eventId,
            participantName = participantName,
            itemsAssigned = itemsAssigned,
            onSuccess = {
                getEventDetails(eventId) // Perbarui data event setelah participant ditambahkan
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun updateParticipantItems(
        eventId: String,
        participantId: String,
        itemsAssigned: List<String>
    ) {
        repository.updateParticipantItems(
            eventId = eventId,
            participantId = participantId,
            itemsAssigned = itemsAssigned,
            onSuccess = {
                getEventDetails(eventId) // Perbarui data event setelah update
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

//    private val _userProfile = MutableLiveData<UserProfile?>()
//    val userProfile: LiveData<UserProfile?> get() = _userProfile
//
//    fun getUserProfile() {
//        repository.getUserProfile(
//            onSuccess = { profile ->
//                _userProfile.value = profile
//            },
//            onFailure = { e ->
//                _error.value = e.message
//            }
//        )
//    }
}