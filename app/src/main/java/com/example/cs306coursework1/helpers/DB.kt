package com.example.cs306coursework1.helpers

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.SubmissionType
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DB {
    companion object {
        private val db = Firebase.firestore

        fun getUserByUID(uid: String): Task<QuerySnapshot> {
            return db.collection("users").whereEqualTo("uid", uid).get()
        }

        fun getUsersByUIDs(uids: ArrayList<String>): Task<QuerySnapshot> {
            return db.collection("users").whereIn("uid", uids).get()
        }

        fun createNewUser(
            uid: String,
            name: String,
            accountType: AccountType
        ): Task<DocumentReference> {
            val data = hashMapOf(
                "uid" to uid,
                "name" to name,
                "type" to accountType.type
            )

            return db.collection("users").add(data)
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
                .addOnSuccessListener {
                    val docRef = it.first().reference.id

                    db.collection("artefact_details").document(docRef).delete()
                        .addOnSuccessListener {
                            db.collection("artefacts").document(artefact_id).delete()
                                .addOnSuccessListener {
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

            return taskCompletionSource.task
        }

        fun getSubmissionsByStatus(status: SubmissionType): Task<QuerySnapshot> {
            return db.collection("submissions")
                .whereEqualTo("status", status.toString().lowercase()).get()
        }
    }
}