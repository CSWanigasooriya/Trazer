package com.flaze.trazer.model

data class Message(
    val id: String,
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