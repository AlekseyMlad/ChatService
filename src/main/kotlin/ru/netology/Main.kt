package ru.netology

data class Message(
    val id: Int = 0,
    val senderId: Int,
    val text: String,
    var isRead: Boolean = false,
    var isDeleted: Boolean = false
)
data class Chat(
    val id: Int = 0,
    val user1Id: Int,
    val user2Id: Int,
    var isDeleted: Boolean = false,
    val messages: MutableList<Message> = mutableListOf()
)

class ChatNotFoundException(message: String) : RuntimeException(message)
class MessageNotFoundException(message: String) : RuntimeException(message)

object ChatService {
    val chats = mutableMapOf<Int, Chat>()
    private var nextChatId = 1
    private var nextMessageId = 1

    fun createChat(user1Id: Int, user2Id: Int): Int {
        chats.values.find { (it.user1Id == user1Id && it.user2Id == user2Id) || (it.user1Id == user2Id && it.user2Id == user1Id) }?.let { return it.id }
        val newChat = Chat(id = nextChatId, user1Id = user1Id, user2Id = user2Id)
        chats[nextChatId] = newChat
        return nextChatId++
    }

    fun deleteChat(chatId: Int): Boolean {
        chats[chatId]?.takeUnless { it.isDeleted } ?: throw ChatNotFoundException("Чат не найден или уже удален")
        chats[chatId] = chats[chatId]!!.copy(isDeleted = true, messages = mutableListOf())
        return true
    }

    fun getChats(userId: Int): List<Chat> = chats.values.filter { (it.user1Id == userId || it.user2Id == userId) && !it.isDeleted }

    fun getUnreadChatsCount(): Int = chats.values.count { chat -> chat.messages.any { !it.isRead } }

    fun addMessage(chatId: Int, senderId: Int, text: String) {
        val chat = chats.getOrPut(chatId) {
            val newChat = Chat(id = nextChatId++, user1Id = senderId, user2Id = senderId)
            newChat
        }
        val newMessage = Message(id = nextMessageId++, senderId = senderId, text = text)
        chat.messages.add(newMessage)
    }

    fun editMessage(chatId: Int, messageId: Int, newText: String): Boolean {
        val chat = chats[chatId] ?: throw ChatNotFoundException("Чат не найден")
        val message = chat.messages.find { it.id == messageId && !it.isDeleted } ?: throw MessageNotFoundException("Сообщение не найдено или удалено")
        chat.messages[chat.messages.indexOf(message)] = message.copy(text = newText)
        return true
    }

    fun deleteMessage(chatId: Int, messageId: Int): Boolean {
        val chat = chats[chatId] ?: throw ChatNotFoundException("Чат не найден")
        val message = chat.messages.find { it.id == messageId && !it.isDeleted } ?: throw MessageNotFoundException("Сообщение не найдено или удалено")
        chat.messages[chat.messages.indexOf(message)] = message.copy(isDeleted = true)
        return true
    }

    fun getLastMessagesFromChats(): List<String> = chats.values.map { chat -> chat.messages.lastOrNull()?.text ?: "нет сообщений" }

    fun getMessagesFromChat(chatId: Int, count: Int): List<Message> {
        val chat = chats[chatId] ?: throw ChatNotFoundException("Чат не найден")
        return chat.messages.takeLast(count).onEach { it.isRead = true }
    }

    fun clear() {
        chats.clear()
        nextChatId = 1
        nextMessageId = 1
    }
}



fun main() {

    val chatId1 = ChatService.createChat(1, 2)
    println("Создан чат с ID: $chatId1")

    ChatService.addMessage(chatId1, 1, "Привет!")

    val userChats = ChatService.getChats(1)
    println("Список чатов для пользователя 1: $userChats")

    val messageId = ChatService.chats[chatId1]!!.messages[0].id // Берем ID первого сообщения
    ChatService.editMessage(chatId1, messageId, "Привет! Как дела?")

    val chatMessages = ChatService.getMessagesFromChat(chatId1, 10)
    println("Сообщения из чата $chatId1: $chatMessages")

    val unreadCount = ChatService.getUnreadChatsCount()
    println("Количество непрочитанных чатов: $unreadCount")

    val lastMessages = ChatService.getLastMessagesFromChats()
    println("Последние сообщения из всех чатов: $lastMessages")

    ChatService.deleteMessage(chatId1, messageId)

    ChatService.deleteChat(chatId1)
    println("Чат $chatId1 удален")
}