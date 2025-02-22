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
        service.createMessage(chatId1, 2, "Hello")
        service.createMessage(chatId2, 3, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun getUnreadChatsCountNoUnread() {
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.createMessage(chatId1, 1, "Hello")
        service.createMessage(chatId2, 1, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun createMessage() {
        val chatId = service.createChat(1, 2)
        assertEquals("Hello", service.createMessage(chatId, 1, "Hello").text)
    }

    @Test(expected = ChatNotFoundException::class)
    fun createMessageChatNotFound() {
        service.createMessage(1, 1, "Hello")
    }

    @Test
    fun getLastMessagesFromChats() {
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.createMessage(chatId1, 2, "Hello")
        service.createMessage(chatId2, 3, "Hi")
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
        service.createMessage(chatId, 2, "Hello")
        service.createMessage(chatId, 2, "Hi")
        val messages = service.getMessagesFromChat(chatId, 2)
        assertEquals(2, messages.size)
        assertTrue(messages.all { it.isRead })
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessagesFromChat_chatNotFound() {
        service.getMessagesFromChat(1, 2)
    }

    @Test
    fun deleteMessage() {
        val chatId = service.createChat(1, 2)
        val message = service.createMessage(chatId, 2, "Hello")
        assertTrue(service.deleteMessage(chatId, message.id))
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessageMessageNotFound() {
        service.deleteMessage(1, 1)
    }

    @Test
    fun editMessage() {
        val chatId = service.createChat(1, 2)
        val message = service.createMessage(chatId, 2, "Hello")
        assertTrue(service.editMessage(chatId, message.id, "New Hello"))
        val messages = service.getMessagesFromChat(chatId, 1)
        assertEquals("New Hello", messages[0].text)
    }

    @Test(expected = MessageNotFoundException::class)
    fun editMessageMessageNotFound() {
        val service = ChatService
        service.clear()
        val chatId = service.createChat(1, 2)
        service.editMessage(chatId, 1, "New Hello")
    }

    @Test
    fun addMessage() {
        val service = ChatService
        service.clear()
        val chatId = service.createChat(1, 2)
        val newMessage = Message(text = "Hello")
        service.addMessage(chatId, newMessage)
        assertEquals(1, ChatService.chats[chatId]!!.messages.size)
        assertEquals("Hello", ChatService.chats[chatId]!!.messages[0].text)
    }

    @Test
    fun unreadChatsCountTest() {
        val service = ChatService
        service.clear()
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.createMessage(chatId1, 2, "Hello")
        service.createMessage(chatId2, 3, "Hi")
        assertEquals(2, service.getUnreadChatsCount())
    }

    @Test
    fun lastMessagesTest() {
        val service = ChatService
        service.clear()
        val chatId1 = service.createChat(1, 2)
        val chatId2 = service.createChat(1, 3)
        service.createMessage(chatId1, 2, "Hello")
        service.createMessage(chatId2, 3, "Hi")
        assertEquals(listOf("Hello", "Hi"), service.getLastMessagesFromChats())
    }

    @Test
    fun getMessagesTest() {
        val service = ChatService
        service.clear()
        val chatId = service.createChat(1, 2)
        service.createMessage(chatId, 2, "Hello")
        service.createMessage(chatId, 2, "Hi")
        val messages = service.getMessagesFromChat(chatId, 2)
        assertEquals(2, messages.size)
        assertTrue(messages.all { it.isRead })
    }
}