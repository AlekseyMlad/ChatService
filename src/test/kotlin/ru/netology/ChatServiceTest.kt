package ru.netology

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before


class ChatServiceTest {
    val service = ChatService

    @Before
    fun Clear() {
        service.clear()
    }

    @Test
    fun createChat() {
        assertEquals(1, service.createChat(1, 2))
    }

    @Test
    fun createChatAlreadyExists() {
        service.createChat(1, 2)
        assertEquals(1, service.createChat(1, 2))
    }

    @Test
    fun deleteChat() {
        val chatId = service.createChat(1, 2)
        assertTrue(service.deleteChat(chatId))
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChatChatNotFound() {
        service.deleteChat(1)
    }

    @Test
    fun getChats() {
        service.createChat(1, 2)
        service.createChat(1, 3)
        assertEquals(2, service.getChats(1).size)
    }

    @Test
    fun getChatsNoChats() {
        assertEquals(0, service.getChats(1).size)
    }

    @Test
    fun getUnreadChatsCount() {
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.addMessage(chatId1,2, "Hello")
        service.addMessage(chatId2, 3, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun getUnreadChatsCountNoUnread() {
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.addMessage(chatId1, 1, "Hello")
        service.addMessage(chatId2, 1, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun createMessage() {
        service.addMessage(1, 1, "Hello")
        val chat = ChatService.chats[1]
        assertEquals(1, chat?.messages?.size)
        assertEquals("Hello", chat?.messages?.get(0)?.text)
    }

    @Test
    fun getLastMessagesFromChats() {
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.addMessage(chatId1, 2, "Hello")
        service.addMessage(chatId2, 3, "Hi")
        assertEquals(listOf("Hello", "Hi"), service.getLastMessagesFromChats())
    }

    @Test
    fun getLastMessagesFromChatsNoMessages() {
        service.createChat(1, 2)
        service.createChat(1, 3)
        assertEquals(listOf("нет сообщений", "нет сообщений"), service.getLastMessagesFromChats())
    }

    @Test
    fun getMessagesFromChat() {
        val chatId = service.createChat(1, 2)
        service.addMessage(chatId, 2, "Hello")
        service.addMessage(chatId, 2, "Hi")
        val messages = service.getMessagesFromChat(chatId, 2)
        assertEquals(2, messages.size)
        assertTrue(messages.all { it.isRead })
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessagesFromChatСhatNotFound() {
        service.getMessagesFromChat(1, 2)
    }

    @Test
    fun deleteMessage() {
        service.addMessage(1, 2, "Hello")
        val chatId = 1
        val messageId = ChatService.chats[chatId]!!.messages[0].id
        assertTrue(service.deleteMessage(chatId,messageId))
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessageMessageNotFound() {
        service.deleteMessage(1, 1)
    }

    @Test
    fun editMessage() {
        service.addMessage(1, 2, "Hello")
        val chatId = 1
        val messageId = ChatService.chats[chatId]!!.messages[0].id
        assertTrue(service.editMessage(chatId, messageId, "New Hello"))
        val messages = service.getMessagesFromChat(chatId, 1)
        assertEquals("New Hello", messages[0].text)
    }

    @Test(expected = MessageNotFoundException::class)
    fun editMessageMessageNotFound() {
//        val service = ChatService
//        service.clear()
        val chatId = service.createChat(1, 2)
        service.editMessage(chatId, 1, "New Hello")
    }

    @Test
    fun addMessageTest() {
        service.addMessage(1, 2, "Hello")
        val chat = ChatService.chats[1]
        assertEquals(1, chat?.messages?.size)
        assertEquals("Hello", chat?.messages?.get(0)?.text)
    }

    @Test
    fun unreadChatsCountTest() {
        service.addMessage(1, 2, "Hello")
        service.addMessage(2, 3, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun lastMessagesTest() {
        service.addMessage(1, 2, "Hello")
        service.addMessage(2, 3, "Hi")
        assertEquals(listOf("Hello", "Hi"), service.getLastMessagesFromChats())
    }


    @Test
    fun getMessagesTest() {
        service.addMessage(1, 2, "Hello")
        service.addMessage(1, 2, "Hi")
        val messages = service.getMessagesFromChat(1, 2)
        assertEquals(2, messages.size)
        assertTrue(messages.all { it.isRead })
    }
}