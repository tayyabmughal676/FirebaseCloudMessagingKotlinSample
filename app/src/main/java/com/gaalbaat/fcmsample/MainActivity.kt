package com.gaalbaat.fcmsample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.gaalbaat.fcmsample.network.RetrofitInstance
import com.gaalbaat.fcmsample.notification.model.NotificationData
import com.gaalbaat.fcmsample.notification.model.PushNotification
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    companion object {
        const val TOPIC = "/topics/myTopic"
    }

    private lateinit var mTitle: EditText
    private lateinit var mMessage: EditText
    private lateinit var mToken: EditText
    private lateinit var mSendBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTitle = findViewById(R.id.notificationTitle)
        mMessage = findViewById(R.id.notificationMessage)
        mToken = findViewById(R.id.notificationToken)
        mSendBtn = findViewById(R.id.sendBtn)

        FirebaseService.sharedRref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isComplete) {

                val token = task.result

                mToken.setText(token)
                FirebaseService.token = token
            }

        }

//        Firebase Messaging Subscribe Topic
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        mSendBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        val title = mTitle.text.toString().trim()
        val message = mMessage.text.toString().trim()
        val reveiverToken = mToken.text.toString().trim()

        if (title.isNotEmpty() && message.isNotEmpty()) {
            PushNotification(
                NotificationData(title, message),
                TOPIC
            ).also {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO)
        .launch {
            try {
                val response =
                    RetrofitInstance
                        .api
                        .postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse: $response. ")
                } else {
                    Log.d("TAG", "Response Failed: ${response.errorBody().toString()} ")
                }

            } catch (e: Exception) {
                Log.d("TAG", "sendNotification: $e")
            }
        }
}