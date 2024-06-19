@file:Suppress("DEPRECATION")

package com.example.daytask.feature.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daytask.DayTaskApplication
import com.example.daytask.core.common.ext.UiAction
import com.example.daytask.core.common.util.Constants
import com.example.daytask.core.common.util.Constants.TIME_CHANGED
import com.example.daytask.core.common.util.Constants.backgroundRGB
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.ui.presentation.LoadingDialog
import com.example.daytask.core.ui.presentation.LoadingScreen
import com.example.daytask.core.ui.theme.DayTaskTheme
import com.example.daytask.feature.auth.AuthDestination
import com.example.daytask.feature.auth.AuthScreen
import com.example.daytask.feature.auth.ext.AuthAction
import com.example.daytask.feature.splash.SplashDestination
import com.example.daytask.feature.splash.SplashScreen
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first


class MainActivity : ComponentActivity() {
    companion object {
        private const val LOADING_ROUTE = "loading"
        private const val AUTH_RESULT = "auth_result"
    }

    private lateinit var auth: FirebaseAuth
    private var load by mutableStateOf(false)

    private val timeChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (action != null && action == Intent.ACTION_TIME_TICK) {
                LocalBroadcastManager.getInstance(this@MainActivity)
                    .sendBroadcast(Intent(TIME_CHANGED))
            }
        }
    }
    private var intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleResult: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        initGoogle()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = backgroundRGB
        window.navigationBarColor = backgroundRGB

        setContent {
            DayTaskTheme {
                Surface(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainNavController = rememberNavController()

                    LaunchedEffect(key1 = "start") {
                        mainNavController.navigate(getStartRoute()) {
                            popUpTo(LOADING_ROUTE) {
                                inclusive = true
                            }
                        }
                        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
                            object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    if (intent?.action == AUTH_RESULT) {
                                        mainNavController.navigate(MainDestination.route) {
                                            popUpTo(AuthDestination.route) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            },
                            IntentFilter(AUTH_RESULT),
                        )
                    }

                    if (load) {
                        LoadingDialog()
                    }

                    NavHost(
                        navController = mainNavController,
                        startDestination = LOADING_ROUTE
                    ) {
                        composable(route = LOADING_ROUTE) {
                            LoadingScreen(modifier = Modifier.fillMaxWidth())
                        }
                        composable(route = SplashDestination.route) {
                            SplashScreen(
                                onAction = {
                                    mainNavController.navigate(AuthDestination.route) {
                                        popUpTo(SplashDestination.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(route = AuthDestination.route) {
                            AuthScreen(
                                onAction = ::consumeAuthAction,
                            )
                        }
                        composable(route = MainDestination.route) {
                            DayTaskApp(
                                navigateToStart = {
                                    mainNavController.navigate(AuthDestination.route) {
                                        popUpTo(MainDestination.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initGoogle() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(Constants.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
        googleResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(data)
                        val idToken = credential.googleIdToken
                        if (idToken != null) {
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        userToDatabase()
                                        sendAuthResult()
                                    } else errorToast(task.exception!!)
                                }
                        } else errorToast(Exception("Unknown error")) /* Shouldn't happen */
                    } catch (e: ApiException) {
                        errorToast(e)
                    }
                } else errorToast(Exception("Cancelled"))
            }
    }

    private fun signUp(
        email: String,
        password: String,
        name: String
    ) {
        load = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) createProfile(name)
                else errorToast(task.exception!!)
            }
    }

    private fun logIn(
        email: String,
        password: String
    ) {
        load = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendAuthResult()
                } else errorToast(task.exception!!)
            }
    }

    private fun googleSignIn() {
        load = true
        oneTapClient.beginSignIn(signInRequest)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        googleResult.launch(
                            IntentSenderRequest.Builder(task.result.pendingIntent).build()
                        )
                    } catch (e: Exception) {
                        errorToast(e)
                    }
                } else errorToast(task.exception!!)
            }
    }

    private fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully sent", Toast.LENGTH_SHORT).show()
                } else errorToast(task.exception!!)
            }
    }

    private fun consumeAuthAction(action: UiAction) {
        when (action) {
            is AuthAction.SignUp -> signUp(action.email, action.password, action.name)
            is AuthAction.LogIn -> logIn(action.email, action.password)
            is AuthAction.ForgotPassword -> forgotPassword(action.email)
            AuthAction.OnClick.GOOGLE_SIGN_IN -> googleSignIn()

            else -> {
                // stub
            }
        }
    }

    private fun createProfile(name: String) {
        val profile = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        auth.currentUser?.updateProfile(profile)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userToDatabase()
                    sendAuthResult()
                } else errorToast(task.exception!!)
            }
    }

    private fun userToDatabase() {
        with(FirebaseManager) {
            updateUserName(auth.currentUser?.displayName)
            updateUserPhoto(auth.currentUser?.photoUrl.toString())
        }
    }

    private suspend fun getStartRoute(): String {
        return if (auth.currentUser != null) {
            MainDestination.route
        } else {
            val application = this.application as? DayTaskApplication
            val repository = application?.container?.firstTimeRepository
            return if (repository?.firstTime?.first() == true) {
                repository.saveFT(false)
                SplashDestination.route
            } else {
                AuthDestination.route
            }
        }
    }

    private fun sendAuthResult() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(AUTH_RESULT))
        load = false
    }

    private fun errorToast(exception: Exception) {
        Toast.makeText(
            applicationContext,
            "Authentication failed. ${exception.message}",
            Toast.LENGTH_SHORT,
        ).show()
        load = false
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(timeChangedReceiver, intentFilter)
        FirebaseManager.updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(timeChangedReceiver)
        FirebaseManager.updateUserStatus(false)
    }
}