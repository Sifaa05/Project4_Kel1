package com.example.billbuddy.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import android.util.Log
import com.google.android.gms.tasks.Tasks
//import com.google.firebase.auth.FirebaseAuth

class SplitBillRepository {
    private val db = FirebaseFirestore.getInstance()

    fun createSplitEvent(
        creatorId: String,
        creatorName: String,
        eventName: String,
        items: List<Item>,
        participants: List<Participant>,
        splitType: String,
        taxAmount: Long = 0,
        serviceFee: Long = 0,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val totalItemPrice = items.sumOf { it.price * it.quantity }
        val finalTaxAmount = taxAmount
        val finalServiceFee = serviceFee
        val totalAmount = totalItemPrice + finalTaxAmount + finalServiceFee

        val eventData = hashMapOf(
            "creator_id" to creatorId,
            "creator_name" to creatorName,
            "event_name" to eventName,
            "total_amount" to totalAmount,
            "tax_amount" to finalTaxAmount,
            "service_fee" to finalServiceFee,
            "status" to "ongoing",
            "timestamp" to System.currentTimeMillis() / 1000,
            "share_link" to "https://billbuddy.app/event/$eventName"
        )

        db.collection("split_events").add(eventData)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore", "Event created with ID: ${docRef.id}")
                val eventId = docRef.id
                val itemsCollection = docRef.collection("items")
                val participantsCollection = docRef.collection("participants")

                // Simpan items secara berurutan dan pastikan semua item tersimpan
                val itemTasks = items.map { item ->
                    itemsCollection.add(item)
                }

                // Tunggu semua item tersimpan sebelum melanjutkan
                com.google.android.gms.tasks.Tasks.whenAllComplete(itemTasks)
                    .addOnSuccessListener {
                        // Hitung pembagian tagihan
                        val updatedParticipants = participants.map { participant ->
                            val amount = if (splitType == "even") {
                                totalAmount / participants.size
                            } else {
                                val assignedItems = items.filter { participant.itemsAssigned?.contains(it.itemId) == true }
                                val itemTotal = assignedItems.sumOf { it.price * it.quantity }
                                val taxPortion = if (finalTaxAmount > 0) (itemTotal * finalTaxAmount / totalItemPrice) else 0
                                val servicePortion = if (finalServiceFee > 0) (itemTotal * finalServiceFee / totalItemPrice) else 0
                                itemTotal + taxPortion + servicePortion
                            }
                            participant.copy(amount = amount)
                        }

                        // Simpan participants
                        updatedParticipants.forEach { participant ->
                            participantsCollection.document(participant.id).set(participant)
                        }

                        onSuccess(eventId)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error saving items: $e")
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating event: $e")
                onFailure(e)
            }
    }

    fun getEventDetails(
        eventId: String,
        onSuccess: (EventData) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events").document(eventId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val itemsCollection = doc.reference.collection("items")
                    val participantsCollection = doc.reference.collection("participants")

                    itemsCollection.get().addOnSuccessListener { itemsSnapshot ->
                        val items = itemsSnapshot.documents.mapNotNull { itemDoc ->
                            try {
                                Item(
                                    itemId = itemDoc.id,
                                    name = itemDoc.getString("name") ?: "",
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    price = itemDoc.getLong("price") ?: 0
                                )
                            } catch (e: Exception) {
                                Log.e("Firestore", "Error parsing item: ${e.message}")
                                null
                            }
                        }

                        participantsCollection.get().addOnSuccessListener { participantsSnapshot ->
                            val participants = participantsSnapshot.documents.mapNotNull { participantDoc ->
                                try {
                                    Participant(
                                        id = participantDoc.id,
                                        name = participantDoc.getString("name") ?: "",
                                        userId = participantDoc.getString("userId"),
                                        amount = participantDoc.getLong("amount") ?: 0,
                                        paid = participantDoc.getBoolean("paid") ?: false,
                                        itemsAssigned = participantDoc.get("itemsAssigned") as? List<String>,
                                        isCreator = participantDoc.id == doc.getString("creator_id")
                                    )
                                } catch (e: Exception) {
                                    Log.e("Firestore", "Error parsing participant: ${e.message}")
                                    null
                                }
                            }

                            val eventData = EventData(
                                eventId = doc.id,
                                creatorId = doc.getString("creator_id") ?: "",
                                creatorName = doc.getString("creator_name") ?: "",
                                eventName = doc.getString("event_name") ?: "",
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.getLong("timestamp") ?: 0,
                                shareLink = doc.getString("share_link"),
                                items = items,
                                participants = participants
                            )
                            onSuccess(eventData)
                        }.addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching participants: $e")
                            onFailure(e)
                        }
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching items: $e")
                        onFailure(e)
                    }
                } else {
                    onFailure(Exception("Event not found"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching event: $e")
                onFailure(e)
            }
    }

    fun updatePaymentStatus(
        eventId: String,
        participantId: String,
        paid: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events").document(eventId)
            .collection("participants").document(participantId)
            .update("paid", paid)
            .addOnSuccessListener {
                db.collection("split_events").document(eventId)
                    .collection("participants").get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.documents.all { it.getBoolean("paid") == true }) {
                            db.collection("split_events").document(eventId)
                                .update("status", "completed")
                        }
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun getAllEvents(
        onSuccess: (List<EventData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("SplitBillRepository", "Mengambil semua event dari Firestore")
        db.collection("split_events")
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("SplitBillRepository", "Jumlah dokumen ditemukan: ${snapshot.documents.size}")
                val eventTasks = snapshot.documents.map { doc ->
                    Log.d("SplitBillRepository", "Mengambil subkoleksi untuk event: ${doc.id}")
                    val itemsTask = doc.reference.collection("items").get()
                    val participantsTask = doc.reference.collection("participants").get()

                    Tasks.whenAllSuccess<Any>(itemsTask, participantsTask).continueWith { task ->
                        try {
                            val itemsSnapshot = itemsTask.result
                            val participantsSnapshot = participantsTask.result
                            Log.d("SplitBillRepository", "Items: ${itemsSnapshot?.documents?.size}, Participants: ${participantsSnapshot?.documents?.size}")

                            val items = itemsSnapshot?.documents?.mapNotNull { itemDoc ->
                                Item(
                                    itemId = itemDoc.id,
                                    name = itemDoc.getString("name") ?: "",
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    price = itemDoc.getLong("price") ?: 0
                                )
                            } ?: emptyList()

                            val participants = participantsSnapshot?.documents?.mapNotNull { participantDoc ->
                                Participant(
                                    id = participantDoc.id,
                                    name = participantDoc.getString("name") ?: "",
                                    userId = participantDoc.getString("userId"),
                                    amount = participantDoc.getLong("amount") ?: 0,
                                    paid = participantDoc.getBoolean("paid") ?: false,
                                    itemsAssigned = participantDoc.get("itemsAssigned") as? List<String>,
                                    isCreator = participantDoc.id == doc.getString("creator_id")
                                )
                            } ?: emptyList()

                            EventData(
                                eventId = doc.id,
                                creatorId = doc.getString("creator_id") ?: "",
                                creatorName = doc.getString("creator_name") ?: "",
                                eventName = doc.getString("event_name") ?: "",
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.getLong("timestamp") ?: 0,
                                shareLink = doc.getString("share_link"),
                                items = items,
                                participants = participants
                            )
                        } catch (e: Exception) {
                            Log.e("SplitBillRepository", "Error memproses event ${doc.id}: ${e.message}")
                            null
                        }
                    }
                }

                Tasks.whenAllSuccess<EventData?>(eventTasks).addOnSuccessListener { events ->
                    Log.d("SplitBillRepository", "Berhasil mengambil ${events.size} event")
                    onSuccess(events.filterNotNull())
                }.addOnFailureListener { e ->
                    Log.e("SplitBillRepository", "Gagal mengambil event: ${e.message}")
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("SplitBillRepository", "Gagal mengambil dokumen: ${e.message}")
                onFailure(e)
            }
    }

    fun searchEvents(
        query: String,
        onSuccess: (List<EventData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events")
            .whereGreaterThanOrEqualTo("event_name", query)
            .whereLessThanOrEqualTo("event_name", query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val eventTasks = snapshot.documents.map { doc ->
                    val itemsTask = doc.reference.collection("items").get()
                    val participantsTask = doc.reference.collection("participants").get()

                    Tasks.whenAllSuccess<Any>(itemsTask, participantsTask).continueWith { task ->
                        try {
                            val itemsSnapshot = itemsTask.result
                            val participantsSnapshot = participantsTask.result

                            val items = itemsSnapshot?.documents?.mapNotNull { itemDoc ->
                                Item(
                                    itemId = itemDoc.id,
                                    name = itemDoc.getString("name") ?: "",
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    price = itemDoc.getLong("price") ?: 0
                                )
                            } ?: emptyList()

                            val participants = participantsSnapshot?.documents?.mapNotNull { participantDoc ->
                                Participant(
                                    id = participantDoc.id,
                                    name = participantDoc.getString("name") ?: "",
                                    userId = participantDoc.getString("userId"),
                                    amount = participantDoc.getLong("amount") ?: 0,
                                    paid = participantDoc.getBoolean("paid") ?: false,
                                    itemsAssigned = participantDoc.get("itemsAssigned") as? List<String>,
                                    isCreator = participantDoc.id == doc.getString("creator_id")
                                )
                            } ?: emptyList()

                            EventData(
                                eventId = doc.id,
                                creatorId = doc.getString("creator_id") ?: "",
                                creatorName = doc.getString("creator_name") ?: "",
                                eventName = doc.getString("event_name") ?: "",
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.getLong("timestamp") ?: 0,
                                shareLink = doc.getString("share_link"),
                                items = items,
                                participants = participants
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                Tasks.whenAllSuccess<EventData?>(eventTasks).addOnSuccessListener { events ->
                    onSuccess(events.filterNotNull())
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun getActiveEvents(
        onSuccess: (List<EventData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events")
            .whereEqualTo("status", "ongoing")
            .get()
            .addOnSuccessListener { snapshot ->
                val eventTasks = snapshot.documents.map { doc ->
                    val itemsTask = doc.reference.collection("items").get()
                    val participantsTask = doc.reference.collection("participants").get()

                    Tasks.whenAllSuccess<Any>(itemsTask, participantsTask).continueWith { task ->
                        try {
                            val itemsSnapshot = itemsTask.result
                            val participantsSnapshot = participantsTask.result

                            val items = itemsSnapshot?.documents?.mapNotNull { itemDoc ->
                                Item(
                                    itemId = itemDoc.id,
                                    name = itemDoc.getString("name") ?: "",
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    price = itemDoc.getLong("price") ?: 0
                                )
                            } ?: emptyList()

                            val participants = participantsSnapshot?.documents?.mapNotNull { participantDoc ->
                                Participant(
                                    id = participantDoc.id,
                                    name = participantDoc.getString("name") ?: "",
                                    userId = participantDoc.getString("userId"),
                                    amount = participantDoc.getLong("amount") ?: 0,
                                    paid = participantDoc.getBoolean("paid") ?: false,
                                    itemsAssigned = participantDoc.get("itemsAssigned") as? List<String>,
                                    isCreator = participantDoc.id == doc.getString("creator_id")
                                )
                            } ?: emptyList()

                            EventData(
                                eventId = doc.id,
                                creatorId = doc.getString("creator_id") ?: "",
                                creatorName = doc.getString("creator_name") ?: "",
                                eventName = doc.getString("event_name") ?: "",
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.getLong("timestamp") ?: 0,
                                shareLink = doc.getString("share_link"),
                                items = items,
                                participants = participants
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                Tasks.whenAllSuccess<EventData?>(eventTasks).addOnSuccessListener { events ->
                    onSuccess(events.filterNotNull())
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun addParticipant(
        eventId: String,
        participantName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val participantData = hashMapOf(
            "name" to participantName,
            "userId" to null, // Bisa diisi dengan userId jika ada autentikasi
            "amount" to 0L, // Awalnya 0, bisa dihitung ulang nanti
            "paid" to false,
            "itemsAssigned" to emptyList<String>()
        )

        db.collection("split_events")
            .document(eventId)
            .collection("participants")
            .add(participantData)
            .addOnSuccessListener {
                // Perbarui total amount event jika diperlukan
                recalculateEventTotal(eventId, onSuccess, onFailure)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    private fun recalculateEventTotal(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events")
            .document(eventId)
            .get()
            .addOnSuccessListener { doc ->
                val totalAmount = doc.getLong("total_amount") ?: 0L
                val participantsTask = doc.reference.collection("participants").get()

                participantsTask.addOnSuccessListener { participantsSnapshot ->
                    val participantCount = participantsSnapshot.documents.size
                    if (participantCount > 0) {
                        val amountPerPerson = totalAmount / participantCount
                        val batch = db.batch()

                        participantsSnapshot.documents.forEach { participantDoc ->
                            val participantRef = doc.reference.collection("participants").document(participantDoc.id)
                            batch.update(participantRef, "amount", amountPerPerson)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    } else {
                        onSuccess()
                    }
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val eventRef = db.collection("split_events").document(eventId)

        // Hapus subkoleksi items
        eventRef.collection("items")
            .get()
            .addOnSuccessListener { itemsSnapshot ->
                val batch = db.batch()
                itemsSnapshot.documents.forEach { itemDoc ->
                    batch.delete(itemDoc.reference)
                }

                // Hapus subkoleksi participants
                eventRef.collection("participants")
                    .get()
                    .addOnSuccessListener { participantsSnapshot ->
                        participantsSnapshot.documents.forEach { participantDoc ->
                            batch.delete(participantDoc.reference)
                        }

                        // Commit batch untuk menghapus subkoleksi
                        batch.commit()
                            .addOnSuccessListener {
                                // Hapus dokumen event utama
                                eventRef.delete()
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure(e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addParticipant(
        eventId: String,
        participantName: String,
        itemsAssigned: List<String>, // Tambahkan parameter untuk itemsAssigned
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Ambil daftar item untuk menghitung total biaya participant
        db.collection("split_events")
            .document(eventId)
            .collection("items")
            .get()
            .addOnSuccessListener { itemsSnapshot ->
                // Hitung total biaya berdasarkan item yang diassign
                val totalAmountForParticipant = itemsSnapshot.documents
                    .filter { itemsAssigned.contains(it.id) }
                    .sumOf { doc ->
                        val price = doc.getLong("price") ?: 0L
                        val quantity = doc.getLong("quantity")?.toInt() ?: 0
                        price * quantity
                    }

                // Tambahkan service fee dan tax (proporsional)
                db.collection("split_events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener { eventDoc ->
                        val serviceFee = eventDoc.getLong("service_fee") ?: 0L
                        val taxAmount = eventDoc.getLong("tax_amount") ?: 0L
                        val totalItems = itemsSnapshot.documents.size
                        val assignedItemsCount = itemsAssigned.size

                        // Proporsionalkan service fee dan tax berdasarkan jumlah item yang diassign
                        val participantServiceFee = if (totalItems > 0) (serviceFee * assignedItemsCount) / totalItems else 0L
                        val participantTax = if (totalItems > 0) (taxAmount * assignedItemsCount) / totalItems else 0L

                        val participantTotal = totalAmountForParticipant + participantServiceFee + participantTax

                        // Simpan participant dengan itemsAssigned
                        val participantData = hashMapOf(
                            "name" to participantName,
                            "userId" to null,
                            "amount" to participantTotal,
                            "paid" to false,
                            "itemsAssigned" to itemsAssigned
                        )

                        db.collection("split_events")
                            .document(eventId)
                            .collection("participants")
                            .add(participantData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun updateParticipantItems(
        eventId: String,
        participantId: String,
        itemsAssigned: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Ambil daftar item untuk menghitung total biaya participant
        db.collection("split_events")
            .document(eventId)
            .collection("items")
            .get()
            .addOnSuccessListener { itemsSnapshot ->
                // Hitung total biaya berdasarkan item yang diassign
                val totalAmountForParticipant = itemsSnapshot.documents
                    .filter { itemsAssigned.contains(it.id) }
                    .sumOf { doc ->
                        val price = doc.getLong("price") ?: 0L
                        val quantity = doc.getLong("quantity")?.toInt() ?: 0
                        price * quantity
                    }

                // Tambahkan service fee dan tax (proporsional)
                db.collection("split_events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener { eventDoc ->
                        val serviceFee = eventDoc.getLong("service_fee") ?: 0L
                        val taxAmount = eventDoc.getLong("tax_amount") ?: 0L
                        val totalItems = itemsSnapshot.documents.size
                        val assignedItemsCount = itemsAssigned.size

                        // Proporsionalkan service fee dan tax berdasarkan jumlah item yang diassign
                        val participantServiceFee = if (totalItems > 0) (serviceFee * assignedItemsCount) / totalItems else 0L
                        val participantTax = if (totalItems > 0) (taxAmount * assignedItemsCount) / totalItems else 0L

                        val participantTotal = totalAmountForParticipant + participantServiceFee + participantTax

                        // Update participant dengan itemsAssigned dan amount baru
                        val participantRef = db.collection("split_events")
                            .document(eventId)
                            .collection("participants")
                            .document(participantId)

                        participantRef.update(
                            mapOf(
                                "itemsAssigned" to itemsAssigned,
                                "amount" to participantTotal
                            )
                        )
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
//    fun getUserProfile(
//        onSuccess: (UserProfile) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            onFailure(Exception("Pengguna belum login"))
//            return
//        }
//
//        // Ambil data tambahan dari Firestore (misalnya status keanggotaan)
//        db.collection("users").document(user.uid).get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val userProfile = UserProfile(
//                        name = user.displayName ?: "Pengguna Aplikasi",
//                        isPremium = document.getBoolean("isPremium") ?: false
//                    )
//                    onSuccess(userProfile)
//                } else {
//                    onFailure(Exception("Data pengguna tidak ditemukan"))
//                }
//            }
//            .addOnFailureListener { e ->
//                onFailure(e)
//            }
//    }
}

//package com.example.billbuddy.data
//
//import com.google.firebase.firestore.FirebaseFirestore
//import com.example.billbuddy.model.EventData
//import com.example.billbuddy.model.Item
//import com.example.billbuddy.model.Participant
//import android.util.Log
//
//class SplitBillRepository {
//    private val db = FirebaseFirestore.getInstance()
//
//    fun createSplitEvent(
//        creatorId: String,
//        creatorName: String,
//        eventName: String,
//        items: List<Item>,
//        participants: List<Participant>,
//        splitType: String,
//        taxAmount: Long = 0, // Tambahkan parameter untuk tax
//        serviceFee: Long = 0, // Tambahkan parameter untuk service fee
//        onSuccess: (String) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        val totalItemPrice = items.sumOf { it.price * it.quantity }
//        val finalTaxAmount = taxAmount // Gunakan nilai dari input pengguna
//        val finalServiceFee = serviceFee // Gunakan nilai dari input pengguna
//        val totalAmount = totalItemPrice + finalTaxAmount + finalServiceFee
//
//        val eventData = hashMapOf(
//            "creator_id" to creatorId,
//            "creator_name" to creatorName,
//            "event_name" to eventName,
//            "total_amount" to totalAmount,
//            "tax_amount" to finalTaxAmount,
//            "service_fee" to finalServiceFee,
//            "status" to "ongoing",
//            "timestamp" to System.currentTimeMillis() / 1000,
//            "share_link" to "https://billbuddy.app/event/$eventName" // Placeholder
//        )
//
//        db.collection("split_events").add(eventData)
//            .addOnSuccessListener { docRef ->
//                Log.d("Firestore", "Event created with ID: ${docRef.id}")
//                val eventId = docRef.id
//                val itemsCollection = docRef.collection("items")
//                val participantsCollection = docRef.collection("participants")
//
//                // Simpan items
//                items.forEach { item ->
//                    itemsCollection.add(item)
//                }
//
//                // Hitung pembagian tagihan
//                val updatedParticipants = participants.map { participant ->
//                    val amount = if (splitType == "even") {
//                        totalAmount / participants.size
//                    } else {
//                        val assignedItems = items.filter { participant.itemsAssigned?.contains(it.itemId) == true }
//                        val itemTotal = assignedItems.sumOf { it.price * it.quantity }
//                        val taxPortion = if (finalTaxAmount > 0) (itemTotal * finalTaxAmount / totalItemPrice) else 0
//                        val servicePortion = if (finalServiceFee > 0) (itemTotal * finalServiceFee / totalItemPrice) else 0
//                        itemTotal + taxPortion + servicePortion
//                    }
//                    participant.copy(amount = amount)
//                }
//
//                // Simpan participants
//                updatedParticipants.forEach { participant ->
//                    participantsCollection.document(participant.id).set(participant)
//                }
//
//                onSuccess(eventId)
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error creating event: $e")
//                onFailure(e)
//            }
//    }
//
//    fun getEventDetails(
//        eventId: String,
//        onSuccess: (EventData) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        db.collection("split_events").document(eventId).get()
//            .addOnSuccessListener { doc ->
//                val itemsCollection = doc.reference.collection("items")
//                val participantsCollection = doc.reference.collection("participants")
//
//                itemsCollection.get().addOnSuccessListener { itemsSnapshot ->
//                    val items = itemsSnapshot.documents.map { itemDoc ->
//                        Item(
//                            itemId = itemDoc.id,
//                            name = itemDoc.getString("name") ?: "",
//                            quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
//                            price = itemDoc.getLong("price") ?: 0
//                        )
//                    }
//
//                    participantsCollection.get().addOnSuccessListener { participantsSnapshot ->
//                        val participants = participantsSnapshot.documents.map { participantDoc ->
//                            Participant(
//                                id = participantDoc.id,
//                                name = participantDoc.getString("name") ?: "",
//                                userId = participantDoc.getString("userId"),
//                                amount = participantDoc.getLong("amount") ?: 0,
//                                paid = participantDoc.getBoolean("paid") ?: false,
//                                itemsAssigned = participantDoc.get("itemsAssigned") as? List<String>,
//                                isCreator = participantDoc.id == doc.getString("creator_id")
//                            )
//                        }
//
//                        val eventData = EventData(
//                            eventId = doc.id,
//                            creatorId = doc.getString("creator_id") ?: "",
//                            creatorName = doc.getString("creator_name") ?: "",
//                            eventName = doc.getString("event_name") ?: "",
//                            totalAmount = doc.getLong("total_amount") ?: 0,
//                            taxAmount = doc.getLong("tax_amount") ?: 0,
//                            serviceFee = doc.getLong("service_fee") ?: 0,
//                            status = doc.getString("status") ?: "ongoing",
//                            timestamp = doc.getLong("timestamp") ?: 0,
//                            shareLink = doc.getString("share_link"),
//                            items = items,
//                            participants = participants
//                        )
//                        onSuccess(eventData)
//                    }
//                }
//            }
//            .addOnFailureListener { e -> onFailure(e) }
//    }
//
//    fun updatePaymentStatus(
//        eventId: String,
//        participantId: String,
//        paid: Boolean,
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        db.collection("split_events").document(eventId)
//            .collection("participants").document(participantId)
//            .update("paid", paid)
//            .addOnSuccessListener {
//                // Cek jika semua peserta sudah bayar
//                db.collection("split_events").document(eventId)
//                    .collection("participants").get()
//                    .addOnSuccessListener { snapshot ->
//                        if (snapshot.documents.all { it.getBoolean("paid") == true }) {
//                            db.collection("split_events").document(eventId)
//                                .update("status", "completed")
//                        }
//                        onSuccess()
//                    }
//            }
//            .addOnFailureListener { e -> onFailure(e) }
//    }
//}
//
////package com.example.billbuddy.data
////
////import com.google.firebase.firestore.FirebaseFirestore
////import com.example.billbuddy.model.EventData
////import com.example.billbuddy.model.Item
////import com.example.billbuddy.model.Participant
////import android.util.Log
////
////class SplitBillRepository {
////    private val db = FirebaseFirestore.getInstance()
////
////    fun createSplitEvent(
////        creatorId: String,
////        creatorName: String,
////        eventName: String,
////        items: List<Item>,
////        participants: List<Participant>,
////        splitType: String, // "even" atau "by_item"
////        onSuccess: (String) -> Unit,
////        onFailure: (Exception) -> Unit
////    ) {
////        val totalItemPrice = items.sumOf { it.price * it.quantity }
////        val taxAmount = (totalItemPrice * 0.11).toLong() // Pajak 11%
////        val serviceFee = (totalItemPrice * 0.10).toLong() // Biaya layanan 10%
////        val totalAmount = totalItemPrice + taxAmount + serviceFee
////
////        val eventData = hashMapOf(
////            "creator_id" to creatorId,
////            "creator_name" to creatorName,
////            "event_name" to eventName,
////            "total_amount" to totalAmount,
////            "tax_amount" to taxAmount,
////            "service_fee" to serviceFee,
////            "status" to "ongoing",
////            "timestamp" to System.currentTimeMillis() / 1000,
////            "share_link" to "https://billbuddy.app/event/$eventName" // Placeholder
////        )
////
////        db.collection("split_events").add(eventData)
////            .addOnSuccessListener { docRef ->
////                Log.d("Firestore", "Event created with ID: ${docRef.id}")
////                val eventId = docRef.id
////                val itemsCollection = docRef.collection("items")
////                val participantsCollection = docRef.collection("participants")
////
////                // Simpan items
////                items.forEach { item ->
////                    itemsCollection.add(item)
////                }
////
////                // Hitung pembagian tagihan
////                val updatedParticipants = participants.map { participant ->
////                    val amount = if (splitType == "even") {
////                        totalAmount / participants.size
////                    } else {
////                        val assignedItems = items.filter { participant.itemsAssigned?.contains(it.itemId) == true }
////                        val itemTotal = assignedItems.sumOf { it.price * it.quantity }
////                        val taxPortion = (itemTotal * 0.11).toLong()
////                        val servicePortion = (itemTotal * 0.10).toLong()
////                        itemTotal + taxPortion + servicePortion
////                    }
////                    participant.copy(amount = amount)
////                }
////
////                // Simpan participants
////                updatedParticipants.forEach { participant ->
////                    participantsCollection.document(participant.id).set(participant)
////                }
////
////                onSuccess(eventId)
////            }
////            .addOnFailureListener { e ->
////                Log.e("Firestore", "Error creating event: $e")
////                onFailure(e) }
////    }
////
////    fun getEventDetails(
////        eventId: String,
////        onSuccess: (EventData) -> Unit,
////        onFailure: (Exception) -> Unit
////    ) {
////        db.collection("split_events").document(eventId).get()
////            .addOnSuccessListener { doc ->
////                val itemsCollection = doc.reference.collection("items")
////                val participantsCollection = doc.reference.collection("participants")
////
////                itemsCollection.get().addOnSuccessListener { itemsSnapshot ->
////                    val items = itemsSnapshot.documents.map { itemDoc ->
////                        Item(
////                            itemId = itemDoc.id,
////                            name = itemDoc.getString("name") ?: "",
////                            quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
////                            price = itemDoc.getLong("price") ?: 0
////                        )
////                    }
////
////                    participantsCollection.get().addOnSuccessListener { participantsSnapshot ->
////                        val participants = participantsSnapshot.documents.map { participantDoc ->
////                            Participant(
////                                id = participantDoc.id,
////                                name = participantDoc.getString("name") ?: "",
////                                userId = participantDoc.getString("user_id"),
////                                amount = participantDoc.getLong("amount") ?: 0,
////                                paid = participantDoc.getBoolean("paid") ?: false,
////                                itemsAssigned = participantDoc.get("items_assigned") as? List<String>,
////                                isCreator = participantDoc.id == doc.getString("creator_id")
////                            )
////                        }
////
////                        val eventData = EventData(
////                            eventId = doc.id,
////                            creatorId = doc.getString("creator_id") ?: "",
////                            creatorName = doc.getString("creator_name") ?: "",
////                            eventName = doc.getString("event_name") ?: "",
////                            totalAmount = doc.getLong("total_amount") ?: 0,
////                            taxAmount = doc.getLong("tax_amount") ?: 0,
////                            serviceFee = doc.getLong("service_fee") ?: 0,
////                            status = doc.getString("status") ?: "ongoing",
////                            timestamp = doc.getLong("timestamp") ?: 0,
////                            shareLink = doc.getString("share_link"),
////                            items = items,
////                            participants = participants
////                        )
////                        onSuccess(eventData)
////                    }
////                }
////            }
////            .addOnFailureListener { e -> onFailure(e) }
////    }
////
////    fun updatePaymentStatus(
////        eventId: String,
////        participantId: String,
////        paid: Boolean,
////        onSuccess: () -> Unit,
////        onFailure: (Exception) -> Unit
////    ) {
////        db.collection("split_events").document(eventId)
////            .collection("participants").document(participantId)
////            .update("paid", paid)
////            .addOnSuccessListener {
////                // Cek jika semua peserta sudah bayar
////                db.collection("split_events").document(eventId)
////                    .collection("participants").get()
////                    .addOnSuccessListener { snapshot ->
////                        if (snapshot.documents.all { it.getBoolean("paid") == true }) {
////                            db.collection("split_events").document(eventId)
////                                .update("status", "completed")
////                        }
////                        onSuccess()
////                    }
////            }
////            .addOnFailureListener { e -> onFailure(e) }
////    }
////}