package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncService(
    private val taskRepository: TaskRepository? = null,
    private val financeRepository: FinanceRepository? = null
) {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        try {
            // Firestore has offline persistence enabled by default on Android.
            // We explicitly configure it here to guarantee offline-first capability for Studio OS.
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build()
                )
                .build()
            firestore.firestoreSettings = settings
            Log.d("SyncService", "Offline-first persistence enabled successfully for Firestore.")
        } catch (e: Exception) {
            Log.e("SyncService", "Error configuring Firestore offline settings: ${e.message}")
        }
    }

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Pushes a list of local tasks to Firebase Firestore in real-time.
     */
    fun syncLocalTasksToRemote(tasks: List<Task>) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                val batch = firestore.batch()
                tasks.forEach { task ->
                    val docRef = firestore.collection("users")
                        .document(userId)
                        .collection("tasks")
                        .document(task.id.toString())
                    
                    val taskMap = hashMapOf(
                        "id" to task.id,
                        "title" to task.title,
                        "notes" to task.notes,
                        "category" to task.category,
                        "priority" to task.priority,
                        "isCompleted" to task.isCompleted,
                        "createdAt" to task.createdAt,
                        "completedAt" to task.completedAt
                    )
                    batch.set(docRef, taskMap, SetOptions.merge())
                }
                batch.commit().addOnSuccessListener {
                    Log.d("SyncService", "Successfully synced tasks to remote firestore.")
                }.addOnFailureListener { e ->
                    Log.e("SyncService", "Failed to sync tasks to remote firestore: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("SyncService", "Error syncing tasks: ${e.message}")
            }
        }
    }

    /**
     * Starts real-time listening to tasks in remote Firestore and updates local Room DB.
     */
    fun startRealtimeTaskSync() {
        val userId = currentUserId ?: return
        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("SyncService", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    scope.launch {
                        for (doc in snapshots.documentChanges) {
                            val data = doc.document.data
                            val id = (data["id"] as? Long)?.toInt() ?: continue
                            val title = data["title"] as? String ?: ""
                            val notes = data["notes"] as? String ?: ""
                            val category = data["category"] as? String ?: "Work"
                            val priority = data["priority"] as? String ?: "Medium"
                            val isCompleted = data["isCompleted"] as? Boolean ?: false
                            val createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis()
                            val completedAt = data["completedAt"] as? Long

                            val task = Task(
                                id = id,
                                title = title,
                                notes = notes,
                                category = category,
                                priority = priority,
                                isCompleted = isCompleted,
                                createdAt = createdAt,
                                completedAt = completedAt
                            )

                            // Update local Room database to maintain local cache consistency
                            taskRepository?.insertTask(task)
                        }
                    }
                }
            }
    }

    /**
     * Real-time counter synchronization.
     */
    fun syncCountersToRemote(counters: List<Counter>) {
        val userId = currentUserId ?: return
        scope.launch {
            try {
                val batch = firestore.batch()
                counters.forEach { counter ->
                    val docRef = firestore.collection("users")
                        .document(userId)
                        .collection("counters")
                        .document(counter.id.toString())
                    
                    val counterMap = hashMapOf(
                        "id" to counter.id,
                        "title" to counter.title,
                        "value" to counter.value
                    )
                    batch.set(docRef, counterMap, SetOptions.merge())
                }
                batch.commit().addOnSuccessListener {
                    Log.d("SyncService", "Successfully synced counters to remote firestore.")
                }.addOnFailureListener { e ->
                    Log.e("SyncService", "Failed to sync counters: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("SyncService", "Error syncing counters: ${e.message}")
            }
        }
    }

    /**
     * Listens to real-time counter changes from Firestore.
     */
    fun startRealtimeCounterSync(onCountersUpdated: (List<Counter>) -> Unit) {
        val userId = currentUserId ?: return
        firestore.collection("users")
            .document(userId)
            .collection("counters")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("SyncService", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val counters = snapshots.mapNotNull { doc ->
                        val data = doc.data
                        val id = (data["id"] as? Long)?.toInt() ?: return@mapNotNull null
                        val title = data["title"] as? String ?: ""
                        val value = (data["value"] as? Long)?.toInt() ?: 0
                        Counter(id, title, value)
                    }
                    onCountersUpdated(counters)
                }
            }
    }
}
