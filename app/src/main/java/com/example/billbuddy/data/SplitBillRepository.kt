package com.example.billbuddy.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import android.util.Log

class SplitBillRepository {
    private val db = FirebaseFirestore.getInstance()

    fun createSplitEvent(
        creatorId: String,
        creatorName: String,
        eventName: String,
        items: List<Item>,
        participants: List<Participant>,
        splitType: String, // "even" atau "by_item"
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val totalItemPrice = items.sumOf { it.price * it.quantity }
        val taxAmount = (totalItemPrice * 0.11).toLong() // Pajak 11%
        val serviceFee = (totalItemPrice * 0.10).toLong() // Biaya layanan 10%
        val totalAmount = totalItemPrice + taxAmount + serviceFee

        val eventData = hashMapOf(
            "creator_id" to creatorId,
            "creator_name" to creatorName,
            "event_name" to eventName,
            "total_amount" to totalAmount,
            "tax_amount" to taxAmount,
            "service_fee" to serviceFee,
            "status" to "ongoing",
            "timestamp" to System.currentTimeMillis() / 1000,
            "share_link" to "https://billbuddy.app/event/$eventName" // Placeholder
        )

        db.collection("split_events").add(eventData)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore", "Event created with ID: ${docRef.id}")
                val eventId = docRef.id
                val itemsCollection = docRef.collection("items")
                val participantsCollection = docRef.collection("participants")

                // Simpan items
                items.forEach { item ->
                    itemsCollection.add(item)
                }

                // Hitung pembagian tagihan
                val updatedParticipants = participants.map { participant ->
                    val amount = if (splitType == "even") {
                        totalAmount / participants.size
                    } else {
                        val assignedItems = items.filter { participant.itemsAssigned?.contains(it.itemId) == true }
                        val itemTotal = assignedItems.sumOf { it.price * it.quantity }
                        val taxPortion = (itemTotal * 0.11).toLong()
                        val servicePortion = (itemTotal * 0.10).toLong()
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
                Log.e("Firestore", "Error creating event: $e")
                onFailure(e) }
    }

    fun getEventDetails(
        eventId: String,
        onSuccess: (EventData) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("split_events").document(eventId).get()
            .addOnSuccessListener { doc ->
                val itemsCollection = doc.reference.collection("items")
                val participantsCollection = doc.reference.collection("participants")

                itemsCollection.get().addOnSuccessListener { itemsSnapshot ->
                    val items = itemsSnapshot.documents.map { itemDoc ->
                        Item(
                            itemId = itemDoc.id,
                            name = itemDoc.getString("name") ?: "",
                            quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                            price = itemDoc.getLong("price") ?: 0
                        )
                    }

                    participantsCollection.get().addOnSuccessListener { participantsSnapshot ->
                        val participants = participantsSnapshot.documents.map { participantDoc ->
                            Participant(
                                id = participantDoc.id,
                                name = participantDoc.getString("name") ?: "",
                                userId = participantDoc.getString("user_id"),
                                amount = participantDoc.getLong("amount") ?: 0,
                                paid = participantDoc.getBoolean("paid") ?: false,
                                itemsAssigned = participantDoc.get("items_assigned") as? List<String>,
                                isCreator = participantDoc.id == doc.getString("creator_id")
                            )
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
                    }
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
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
                // Cek jika semua peserta sudah bayar
                db.collection("split_events").document(eventId)
                    .collection("participants").get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.documents.all { it.getBoolean("paid") == true }) {
                            db.collection("split_events").document(eventId)
                                .update("status", "completed")
                        }
                        onSuccess()
                    }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}