package com.example.billbuddy.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query

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
        val subtotal = items.sumOf { it.totalPrice }
        val totalAmount = subtotal + taxAmount + serviceFee


        val eventData = hashMapOf(
            "creator_id" to creatorId,
            "creator_name" to creatorName,
            "event_name" to eventName,
            "subtotal" to subtotal,
            "total_amount" to totalAmount,
            "tax_amount" to taxAmount,
            "service_fee" to serviceFee,
            "status" to "ongoing",
            "timestamp" to Timestamp.now(),
            "share_link" to "https://billbuddy.app/event?eventId=$eventName"
        )

        db.collection("split_events").add(eventData)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore", "Event created with ID: ${docRef.id}")
                val eventId = docRef.id
                val itemsCollection = docRef.collection("items")
                val participantsCollection = docRef.collection("participants")

                val itemTasks = items.map { item ->
                    itemsCollection.add(
                        hashMapOf(
                            "name" to item.name,
                            "quantity" to item.quantity,
                            "unitPrice" to item.unitPrice,
                            "totalPrice" to item.totalPrice
                        )
                    )
                }

                Tasks.whenAllComplete(itemTasks)
                    .addOnSuccessListener {
                        val updatedParticipants = participants.map { participant ->
                            val amount = if (splitType == "even") {
                                totalAmount / participants.size
                            } else {
                                val assignedItems = items.filter { participant.itemsAssigned?.contains(it.itemId) == true }
                                val itemTotal = assignedItems.sumOf { it.totalPrice }
                                val itemProportion = if (subtotal > 0) itemTotal.toDouble() / subtotal else 0.0
                                val taxPortion = (itemProportion * taxAmount).toLong()
                                val servicePortion = (itemProportion * serviceFee).toLong()
                                itemTotal + taxPortion +servicePortion
                            }

                            participant.copy(
                                amount = amount,
                                paid = participant.isCreator
                            )
                        }

                        updatedParticipants.forEach { participant ->
                            participantsCollection.document(participant.id).set(participant)
                        }

                        docRef.update("share_link", "https://billbuddy.app/event?eventId=$eventId")
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
                                    unitPrice = itemDoc.getLong("unitPrice") ?: 0,
                                    totalPrice = itemDoc.getLong("totalPrice") ?: 0,
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
                                subtotal = doc.getLong("subtotal") ?: 0,
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.get("timestamp") as? Timestamp ?: Timestamp.now(),
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
            .collection("participants").document(participantId).get()
            .addOnSuccessListener { participantDoc ->
                val isCreator = participantDoc.getBoolean("isCreator") ?: false
                if (isCreator) {
                    onFailure(Exception("Cannot update payment status for creator"))
                    return@addOnSuccessListener
                }

                db.collection("split_events").document(eventId)
                    .collection("participants").document(participantId)
                    .update("paid", paid)
                    .addOnSuccessListener {
                        db.collection("split_events").document(eventId)
                            .collection("participants").get()
                            .addOnSuccessListener { snapshot ->
                                val allNonCreatorPaid = snapshot.documents
                                    .filterNot { it.getBoolean("isCreator") == true }
                                    .all { it.getBoolean("paid") == true }
                                if (allNonCreatorPaid) {
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
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
                            Log.d(
                                "SplitBillRepository",
                                "Items: ${itemsSnapshot?.documents?.size}, Participants: ${participantsSnapshot?.documents?.size}")

                            val items = itemsSnapshot?.documents?.mapNotNull { itemDoc ->
                                Item(
                                    itemId = itemDoc.id,
                                    name = itemDoc.getString("name") ?: "",
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    unitPrice = itemDoc.getLong("unitPrice") ?: 0,
                                    totalPrice = itemDoc.getLong("totalPrice") ?: 0
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
                                subtotal = doc.getLong("subtotal") ?: 0,
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.get("timestamp") as? Timestamp ?: Timestamp.now(),
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
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
                                    unitPrice = itemDoc.getLong("unitPrice") ?: 0,
                                    totalPrice = itemDoc.getLong("totalPrice") ?: 0
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
                                subtotal = doc.getLong("subtotal") ?: 0,
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.get("timestamp") as? Timestamp ?: Timestamp.now(),
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
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
                                    unitPrice = itemDoc.getLong("unitPrice") ?: 0,
                                    totalPrice = itemDoc.getLong("totalPrice") ?: 0
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
                                subtotal = doc.getLong("subtotal") ?: 0,
                                totalAmount = doc.getLong("total_amount") ?: 0,
                                taxAmount = doc.getLong("tax_amount") ?: 0,
                                serviceFee = doc.getLong("service_fee") ?: 0,
                                status = doc.getString("status") ?: "ongoing",
                                timestamp = doc.get("timestamp") as? Timestamp ?: Timestamp.now(),
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
        itemsAssigned: List<String>? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events")
            .document(eventId)
            .get()
            .addOnSuccessListener { eventDoc ->
                val subtotal = eventDoc.getLong("subtotal") ?: 0
                val taxAmount = eventDoc.getLong("tax_amount") ?: 0
                val serviceFee = eventDoc.getLong("service_fee") ?: 0
                val totalAmount = eventDoc.getLong("total_amount") ?: 0

                db.collection("split_events")
                    .document(eventId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener { itemsSnapshot ->
                        val participantTotal = if (itemsAssigned.isNullOrEmpty()) {
                            0L
                        } else {
                            val totalAmountForParticipant = itemsSnapshot.documents
                                .filter { itemsAssigned.contains(it.id) }
                                .sumOf { doc ->
                                    val price = doc.getLong("totalPrice") ?: 0L
                                    price
                                }
                            val itemProportion = if (subtotal > 0) totalAmountForParticipant.toDouble() / subtotal else 0.0
                            val taxPortion = (itemProportion * taxAmount).toLong()
                            val servicePortion = (itemProportion * serviceFee).toLong()
                            totalAmountForParticipant + taxPortion + servicePortion
                        }

                        val participantData = hashMapOf(
                            "name" to participantName,
                            "userId" to null,
                            "amount" to participantTotal,
                            "paid" to false,
                            "itemsAssigned" to itemsAssigned,
                            "isCreator" to false
                        )

                        db.collection("split_events")
                            .document(eventId)
                            .collection("participants")
                            .add(participantData)
                            .addOnSuccessListener {
                                if (itemsAssigned.isNullOrEmpty()) {
                                    recalculateEventTotal(eventId, onSuccess, onFailure)
                                } else {
                                    onSuccess()
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
                            if (participantDoc.getBoolean("isCreator") != true) {
                                val participantRef = doc.reference.collection("participants").document(participantDoc.id)
                                batch.update(participantRef, "amount", amountPerPerson)
                            }
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

        eventRef.collection("items")
            .get()
            .addOnSuccessListener { itemsSnapshot ->
                val batch = db.batch()
                itemsSnapshot.documents.forEach { itemDoc ->
                    batch.delete(itemDoc.reference)
                }

                eventRef.collection("participants")
                    .get()
                    .addOnSuccessListener { participantsSnapshot ->
                        participantsSnapshot.documents.forEach { participantDoc ->
                            batch.delete(participantDoc.reference)
                        }

                        batch.commit()
                            .addOnSuccessListener {
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
        itemsAssigned: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events")
            .document(eventId)
            .collection("items")
            .get()
            .addOnSuccessListener { itemsSnapshot ->
                val totalAmountForParticipant = itemsSnapshot.documents
                    .filter { itemsAssigned.contains(it.id) }
                    .sumOf { doc ->
                        val price = doc.getLong("price") ?: 0L
                        val quantity = doc.getLong("quantity")?.toInt() ?: 0
                        price * quantity
                    }

                db.collection("split_events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener { eventDoc ->
                        val serviceFee = eventDoc.getLong("service_fee") ?: 0L
                        val taxAmount = eventDoc.getLong("tax_amount") ?: 0L
                        val totalItems = itemsSnapshot.documents.size
                        val assignedItemsCount = itemsAssigned.size

                        val participantServiceFee = if (totalItems > 0) (serviceFee * assignedItemsCount) / totalItems else 0L
                        val participantTax = if (totalItems > 0) (taxAmount * assignedItemsCount) / totalItems else 0L

                        val participantTotal = totalAmountForParticipant + participantServiceFee + participantTax

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
        db.collection("split_events")
            .document(eventId)
            .get()
            .addOnSuccessListener { eventDoc ->
                val subtotal = eventDoc.getLong("subtotal") ?: 0
                val taxAmount = eventDoc.getLong("tax_amount") ?: 0
                val serviceFee = eventDoc.getLong("service_fee") ?: 0

                db.collection("split_events")
                    .document(eventId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener { itemsSnapshot ->
                        val totalAmountForParticipant = itemsSnapshot.documents
                            .filter { itemsAssigned.contains(it.id) }
                            .sumOf { doc ->
                                val price = doc.getLong("totalPrice") ?: 0L
                                price
                            }

                        val itemProportion = if (subtotal > 0) totalAmountForParticipant.toDouble() / subtotal else 0.0
                        val taxPortion = (itemProportion * taxAmount).toLong()
                        val servicePortion = (itemProportion * serviceFee).toLong()
                        val participantTotal = totalAmountForParticipant + taxPortion + servicePortion

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
}
