package com.example.cs306coursework1.helpers

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.SubmissionType
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DB {
    companion object {
        private val db = Firebase.firestore

        fun getUserByUID(uid: String): Task<QuerySnapshot> {
            return db.collection("users").whereEqualTo("uid", uid).get()
        }

        fun getAllUsers(): Task<QuerySnapshot> {
            return db.collection("users").get()
        }

        fun getUsersByUIDs(uids: ArrayList<String>): Task<QuerySnapshot> {
            return db.collection("users").whereIn("uid", uids).get()
        }

        fun createNewUser(
            uid: String,
            name: String,
            accountType: AccountType
        ): Task<DocumentReference> {
            val taskCompletionSource = TaskCompletionSource<DocumentReference>()
            Firebase.storage.reference.child("profile_images/default.png").downloadUrl.addOnSuccessListener { uri ->
                val data = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "description" to "No description",
                    "level" to 0.0F,
                    "profile_image" to uri.toString(),
                    "type" to accountType.type,
                    "created_at" to Timestamp.now()
                )

                db.collection("users").add(data).addOnSuccessListener { docRef ->
                    taskCompletionSource.setResult(docRef)
                }.addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
            }.addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

            return taskCompletionSource.task
        }

        fun getAvailableMuseums(): Task<QuerySnapshot> {
            return db.collection("museums").get()
        }

        fun getArtefactsOfMuseum(museum_id: String): Task<QuerySnapshot> {
            return db.collection("artefacts").whereEqualTo("museum_id", museum_id)
                .whereEqualTo("isApproved", true).get()
        }

        fun getArtefactDetailsByID(artefact_id: String): Task<QuerySnapshot> {
            return db.collection("artefact_details").whereEqualTo("artefact_id", artefact_id).get()
        }

        fun getArtefactByProperty(
            museum_id: String,
            property: String,
            value: String
        ): Task<QuerySnapshot> {
            return db.collection("artefacts").whereEqualTo("museum_id", museum_id)
                .whereEqualTo(property, value).get()
        }

        fun getArtefactBasicByID(
            artefact_id: String
        ): Task<DocumentSnapshot> {
            return db.collection("artefacts")
                .document(artefact_id).get()
        }

        fun createBasicArtefact(
            data: HashMap<String, Any?>
        ): Task<DocumentReference> {
            return db.collection("artefacts").add(data)
        }

        fun updateBasicArtefact(
            artefact_id: String,
            data: HashMap<String, Any?>
        ): Task<Void> {
            return db.collection("artefacts").document(artefact_id).update(data)
        }

        fun createArtefactDetails(data: HashMap<String, Any?>): Task<DocumentReference> {
            return db.collection("artefact_details").add(data)
        }

        fun updateArtefactDetails(
            artefact_id: String,
            data: HashMap<String, Any?>
        ): Task<Void> {
            val taskCompletionSource = TaskCompletionSource<Void>()

            db.collection("artefact_details").whereEqualTo("artefact_id", artefact_id).get()
                .addOnSuccessListener {
                    val docRef = it.first().reference.id
                    db.collection("artefact_details").document(docRef).update(data)
                        .addOnSuccessListener {
                            taskCompletionSource.setResult(null)
                        }.addOnFailureListener { exception ->
                            taskCompletionSource.setException(exception)
                        }
                }.addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }

            return taskCompletionSource.task
        }

        fun deleteArtefactByID(artefact_id: String): Task<Void> {
            val taskCompletionSource = TaskCompletionSource<Void>()

            db.collection("artefact_details").whereEqualTo("artefact_id", artefact_id).get()
                .addOnSuccessListener { detailsRefs ->
                    val docRef = detailsRefs.first().reference.id

                    db.collection("artefact_details").document(docRef).delete()
                        .addOnSuccessListener {
                            db.collection("artefacts").document(artefact_id).delete()
                                .addOnSuccessListener {
                                    db.collection("submissions")
                                        .whereEqualTo("artefact_id", artefact_id).get()
                                        .addOnSuccessListener { submissionRefs ->
                                            val submissionRef = submissionRefs.first().reference.id
                                            db.collection("submissions").document(submissionRef)
                                                .delete().addOnSuccessListener {
                                                    taskCompletionSource.setResult(null)
                                                }.addOnFailureListener { exception ->
                                                    taskCompletionSource.setException(exception)
                                                }
                                        }.addOnFailureListener { exception ->
                                            taskCompletionSource.setException(exception)
                                        }
                                }.addOnFailureListener { exception ->
                                    taskCompletionSource.setException(exception)
                                }
                        }.addOnFailureListener { exception ->
                            taskCompletionSource.setException(exception)
                        }
                }.addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }

            return taskCompletionSource.task
        }

        fun getSubmissionsByStatus(
            status: SubmissionType,
            museumID: String?,
            userID: String?
        ): Task<QuerySnapshot> {

            if (userID != null && museumID != null) {
                return db.collection("submissions")
                    .whereEqualTo("museum_id", museumID)
                    .whereEqualTo("status", status.toString().lowercase())
                    .whereEqualTo("created_by", userID.toString()).get()
            } else if (userID != null) {
                return db.collection("submissions")
                    .whereEqualTo("status", status.toString().lowercase())
                    .whereEqualTo("created_by", userID.toString()).get()
            }

            return db.collection("submissions")
                .whereEqualTo("museum_id", museumID)
                .whereEqualTo("status", status.toString().lowercase()).get()
        }

        fun createSubmission(data: HashMap<String, Any?>): Task<DocumentReference> {
            return db.collection("submissions").add(data)
        }

        fun updateSubmissions(data: HashMap<String, Any?>): Task<Void> {
            val taskCompletionSource = TaskCompletionSource<Void>()
            Log.d("problem", data.toString())

            db.collection("submissions").whereEqualTo("artefact_id", data["artefact_id"].toString())
                .get().addOnSuccessListener {
                    val doc = it.first()
                    db.collection("submissions").document(doc.id).update(data)
                        .addOnSuccessListener {
                            taskCompletionSource.setResult(null);
                        }.addOnFailureListener { exception ->
                            taskCompletionSource.setException(exception)
                        }
                }.addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
            return taskCompletionSource.task
        }

        fun updateUser(uid: String, data: HashMap<String, Any?>): Task<Void> {
            val taskCompletionSource = TaskCompletionSource<Void>()

            db.collection("users").whereEqualTo("uid", uid).get().addOnSuccessListener { docRefs ->
                val doc = docRefs.first()
                db.collection("users").document(doc.reference.id).update(data)
                    .addOnSuccessListener {
                        taskCompletionSource.setResult(null)
                    }.addOnFailureListener { exception ->
                        taskCompletionSource.setException(exception)
                    }
            }.addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

            return taskCompletionSource.task
        }

        fun createMuseum(data: HashMap<String, Any?>): Task<DocumentReference> {
            return db.collection("museums").add(data)
        }

        fun updateMuseum(museum_id: String, data: HashMap<String, Any?>): Task<Void> {
            return db.collection("museums").document(museum_id).update(data)
        }

        fun getMuseumByID(museum_id: String): Task<DocumentSnapshot> {
            return db.collection("museums")
                .document(museum_id).get()
        }

        fun deleteMuseum(museum_id: String): Task<Void> {
            return db.collection("museums").document(museum_id).delete()
        }
    }
}