package com.ekosoftware.secretdms.data.local

import androidx.lifecycle.LiveData
import com.ekosoftware.secretdms.data.model.Chat
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.injection.DummyData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val messageDao: MessageDao, private val chatDao: ChatDao) {

    suspend fun clearDatabase() {
        messageDao.clearDatabase()
        chatDao.clearDatabase()
    }

    fun getChats(): LiveData<List<ChatPreview>> = messageDao.liveChats()

    fun getChatWithFriendId(friendId: String): LiveData<List<Message>> = messageDao.liveMessagesWithFriendId(friendId)

    suspend fun insertDummyData() = messageDao.insertDummyData(*DummyData.messages)

    suspend fun newChat(friendId: String) = chatDao.insert(Chat(friendId))

    suspend fun insertMessage(message: Message) = messageDao.insert(message)

    suspend fun updateMessagesTime(message: Message) = messageDao.update(message)
}