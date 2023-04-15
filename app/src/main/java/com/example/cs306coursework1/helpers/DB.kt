package com.example.cs306coursework1.helpers

import com.example.cs306coursework1.data.AccountType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DB {
    companion object {
        private val db = Firebase.firestore

        fun getUserByUID(uid: String): Task<QuerySnapshot> {
            return db.collection("users").whereEqualTo("uid", uid).get()
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
            return db.collection("artefacts").whereEqualTo("museum_id", museum_id).get()
        }

        fun getArtefactDetailsByID(artefact_id: String): Task<QuerySnapshot> {
            return db.collection("artefact_details").whereEqualTo("artefact_id", artefact_id).get()
        }

        fun getArtefactByLabel(
            museum_id: String,
            label: String
        ): Task<QuerySnapshot> {
            return db.collection("artefacts").whereEqualTo("museum_id", museum_id)
                .whereEqualTo("label", label).get()
        }

        fun createBasicArtefact(
            data: HashMap<String, Any?>
        ): Task<DocumentReference> {
            return db.collection("artefacts").add(data)
        }

        fun createArtefactDetails(data: HashMap<String, Any?>): Task<DocumentReference> {
            return db.collection("artefact_details").add(data)
        }
    }
}