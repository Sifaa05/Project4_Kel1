package com.example.billbuddy.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.repository.UserRepository
import com.example.billbuddy.data.EventData
import com.example.billbuddy.data.Item
import com.example.billbuddy.data.Participant
import com.example.billbuddy.data.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//import com.example.billbuddy.model.UserProfile

class MainViewModel : ViewModel() {
    private val repository = SplitBillRepository()
    private val userRepository = UserRepository()

    private val _eventData = MutableLiveData<EventData?>()
    val eventData: LiveData<EventData?> get() = _eventData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _allEvents = MutableLiveData<List<EventData>>()
    val allEvents: LiveData<List<EventData>> get() = _allEvents

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _eventHistory = MutableLiveData<List<EventData>>(emptyList())
    val eventHistory: LiveData<List<EventData>> get() = _eventHistory

    private val _monthlyTotals = MutableLiveData<Map<String, Long>>()
    val monthlyTotals: LiveData<Map<String, Long>> get() = _monthlyTotals

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
        participantName: String,
        itemsAssigned: List<String> = emptyList()
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

    fun getUserProfile(onSuccess: (User) -> Unit = {}) {
        userRepository.getUserProfile(
            onSuccess = { user ->
                _userProfile.value = user
                onSuccess(user)
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun updateUsername(username: String) {
        userRepository.updateUsername(
            username = username,
            onSuccess = {
                getUserProfile()
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun uploadProfilePhoto(uri: Uri) {
        userRepository.uploadProfilePhoto(
            uri = uri,
            onSuccess = { photoUrl ->
                getUserProfile()
            },
            onFailure = { e ->
                _error.value = e.message
            }
        )
    }

    fun getEventHistory() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            repository.getEventHistory(
                userId = currentUser.uid,
                onSuccess = { events ->
                    _eventHistory.value = events
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
        } else {
            _error.value = "User not logged in"
        }
    }

    fun getMonthlyTotals() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            repository.getMonthlyTotals(
                userId = currentUser.uid,
                onSuccess = { totals ->
                    _monthlyTotals.value = totals
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
        } else {
            _error.value = "User not logged in"
        }
    }

    fun uploadPaymentProof(eventId: String, participantId: String, uri: Uri) {
        viewModelScope.launch {
            repository.uploadPaymentProof(
                eventId = eventId,
                participantId = participantId,
                uri = uri,
                onSuccess = { proofUrl ->
                    getEventDetails(eventId) // Refresh data setelah upload
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
        }
    }
}