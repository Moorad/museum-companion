package com.example.cs306coursework1.helpers

import android.net.Uri
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime

class Storage {

    companion object {
        private val storage = FirebaseStorage.getInstance()

        fun uploadImage(uri: Uri, folder_name: String, viewForSnack: View): Task<Uri> {
            val imageRef =
                storage.reference.child(folder_name + "/" + LocalDateTime.now().toString())

            return imageRef.putFile(uri).continueWithTask { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {

                        Misc.displaySnackBar(viewForSnack, it.message.toString())
                    }
                }

                imageRef.downloadUrl
            }
        }

        fun deleteImage(url: String): Task<Void> {
            val imageRef =
                storage.getReferenceFromUrl(url)

            return imageRef.delete()
        }
    }
}