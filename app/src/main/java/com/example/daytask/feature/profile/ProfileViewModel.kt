package com.example.daytask.feature.profile

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.Constants
import com.example.daytask.core.common.util.NetworkManager
import com.example.daytask.core.common.util.NotifyManager
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.common.util.firebase.TasksManager
import com.example.daytask.data.model.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID


class ProfileViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ProfileUiState(
                isGoogleAuth = isUserGoogleAuth()
            )
        )
    val uiState = _uiState.asStateFlow()

    private var _tasksList: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasksList = _tasksList.asStateFlow()

    init {
        viewModelScope.launch {
            TasksManager.flow.collectLatest { data ->
                _tasksList.update { data.taskList }
            }
        }
    }

    private fun isUserGoogleAuth(): Boolean =
        Firebase.auth.currentUser?.providerData?.map { it.providerId }?.contains("google.com")
            ?: false

    fun updateUserName(context: Context) {
        if (!NetworkManager.isNetworkAvailable(context)) {
            NotifyManager.notifyUser(context)
            return
        }
        updateStatus(Status.Loading)

        Firebase.auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(_uiState.value.userName.trim())
                .build()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseManager.updateUserName(
                        Firebase.auth.currentUser?.displayName
                    )
                    updateUiState(_uiState.value.copy(updateResult = true))
                } else updateStatus(Status.Done)
                NotifyManager.notifyUser(task, context)
            }
    }

    fun updateUserAvatar(context: Context, bitmap: Bitmap) {
        val user = Firebase.auth.currentUser ?: return
        if (!NetworkManager.isNetworkAvailable(context)) {
            NotifyManager.notifyUser(context)
            return
        }
        updateStatus(Status.Loading)

        val uuid = UUID.randomUUID().toString()
        val imagesFolder = Firebase.storage.reference
            .child("users/${Firebase.auth.uid}/images")
        val oldPhotos = imagesFolder.listAll()
        val imageRef = imagesFolder.child(uuid)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val imageData = byteArrayOutputStream.toByteArray()
        val metadata = StorageMetadata.Builder()
            .setContentType("image")
            .build()

        imageRef.putBytes(imageData, metadata)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageRef.downloadUrl
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                user.updateProfile(
                                    UserProfileChangeRequest.Builder()
                                        .setPhotoUri(task2.result)
                                        .build()
                                )
                                    .addOnCompleteListener { task3 ->
                                        if (task3.isSuccessful) {
                                            val url =
                                                Firebase.auth.currentUser?.photoUrl.toString()
                                            FirebaseManager.updateUserPhoto(url)
                                            oldPhotos.result.items.forEach { it.delete() } // clear old photos
                                            updateUiState(_uiState.value.copy(updateResult = true))
                                        } else updateStatus(Status.Done)
                                        NotifyManager.notifyUser(task3, context)
                                    }
                            } else {
                                NotifyManager.notifyUser(task2, context)
                                updateStatus(Status.Done)
                            }
                        }
                } else {
                    NotifyManager.notifyUser(task, context)
                    updateStatus(Status.Done)
                }
            }

    }

    fun updateUserEmail(context: Context, navigateToStart: () -> Unit) {
        val user = Firebase.auth.currentUser ?: return
        if (!NetworkManager.isNetworkAvailable(context)) {
            NotifyManager.notifyUser(context)
            return
        }
        updateStatus(Status.Loading)

        val password = _uiState.value.userPassword
        val email = user.email.toString()
        val newEmail = _uiState.value.userEmail.trim()
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.verifyBeforeUpdateEmail(newEmail)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Firebase.auth.signOut()
                                navigateToStart()
                            } else updateStatus(Status.Done)
                            NotifyManager.notifyUser(task2, context)
                        }
                } else {
                    NotifyManager.notifyUser(task, context)
                    updateStatus(Status.Done)
                }
            }
    }

    fun updateUserPassword(context: Context, navigateToStart: () -> Unit) {
        val user = Firebase.auth.currentUser ?: return
        if (!NetworkManager.isNetworkAvailable(context)) {
            NotifyManager.notifyUser(context)
            return
        }
        updateStatus(Status.Loading)

        val password = _uiState.value.userPassword
        val newPassword = _uiState.value.newPassword
        val email = user.email.toString()
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Firebase.auth.signOut()
                                navigateToStart()
                            } else updateStatus(Status.Done)
                            NotifyManager.notifyUser(task2, context)
                        }
                } else {
                    NotifyManager.notifyUser(task, context)
                    updateStatus(Status.Done)
                }
            }
    }

    fun updateUiState(uiState: ProfileUiState) = _uiState.update { uiState }

    private fun updateStatus(status: Status) = _uiState.update { it.copy(status = status) }

    fun checkName(): Boolean {
        val name = _uiState.value.userName
        return name.isNotBlank()
                && name.length >= Constants.NAME_LENGTH
                && name != Firebase.auth.currentUser?.displayName
    }

    fun checkEmail(): Boolean {
        val email = _uiState.value.userEmail
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && email != Firebase.auth.currentUser?.email
    }

    fun checkPassword(): Boolean {
        val password = _uiState.value.userPassword
        return password.isNotEmpty()
                && password.length >= Constants.PASSWORD_LENGTH
    }

    fun checkNewPassword(): Boolean {
        val password = _uiState.value.newPassword
        return password.isNotEmpty()
                && password.length >= Constants.PASSWORD_LENGTH
                && password != _uiState.value.userPassword
    }

    fun deleteTask(id: String) {
        FirebaseManager.deleteTask(id).addOnCompleteListener {
            if (!it.isSuccessful) Log.e("Firebase Error", it.exception.toString())
        }
    }
}

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userPassword: String = "",
    val newPassword: String = "",
    val status: Status = Status.Done,
    val updateResult: Boolean = false,
    val isGoogleAuth: Boolean = false,
)