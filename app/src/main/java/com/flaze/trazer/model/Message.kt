package com.flaze.trazer.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat

data class Message(
    val address: String?,
    val body: String?,
    val creator: String?,
    val date: Long,
    val dateSent: Long,
    val errorCode: Int,
    val locked: Int,
    val messageType: Int,
    val person: Long,
    val protocol: Int,
    val read: Int,
    val replyPathPresent: Int,
    val seen: Int,
    val serviceCenter: String?,
    val status: Int,
    val subject: String?,
    val subscriptionId: Long,
    val threadId: Long,
    val type: Int
)

fun readSmsMessages(context: Context): MutableList<Message> {
    val messages = mutableListOf<Message>()

    // Check if permission to read SMS is granted
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED) {

        // Query the SMS inbox to retrieve all columns
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI, null, // Retrieve all columns
            null, null, Telephony.Sms.DEFAULT_SORT_ORDER
        )

        cursor?.use {
            while (it.moveToNext()) {
                // Extracting all possible columns from the cursor
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val creator = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.CREATOR))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val dateSent = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT))
                val errorCode = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.ERROR_CODE))
                val locked = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.LOCKED))
                val messageType = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                val person = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.PERSON))
                val protocol = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.PROTOCOL))
                val read = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.READ))
                val replyPathPresent = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.REPLY_PATH_PRESENT))
                val seen = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.SEEN))
                val serviceCenter = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.SERVICE_CENTER))
                val status = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.STATUS))
                val subject = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.SUBJECT))
                val subscriptionId = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.SUBSCRIPTION_ID))
                val threadId = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                // Create Message object with all the extracted data
                messages.add(
                    Message(
                        address,
                        body,
                        creator,
                        date,
                        dateSent,
                        errorCode,
                        locked,
                        messageType,
                        person,
                        protocol,
                        read,
                        replyPathPresent,
                        seen,
                        serviceCenter,
                        status,
                        subject,
                        subscriptionId,
                        threadId,
                        type
                    )
                )
            }
        }
    }

    return messages
}