package com.pancho.contactomovil2077.Controlador;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {

    private static final String STORAGE_PATH = "tu_directorio_en_firebase_storage/";
    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg"; // Cambia el nombre según tu necesidad

    private StorageReference storageReference;

    public FirebaseStorageHelper() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void subirImagen(Uri imagenUri, String userId, final OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, final OnFailureListener onFailureListener) {
        StorageReference filePath = storageReference.child(STORAGE_PATH).child(userId).child(PROFILE_IMAGE_NAME);

        filePath.putFile(imagenUri)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public StorageReference obtenerReferenciaImagen(String userId) {
        return storageReference.child(STORAGE_PATH).child(userId).child(PROFILE_IMAGE_NAME);
    }
}
