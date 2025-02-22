package ru.netology

data class Message(
    val id: Int = 0,
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

    fun createMessage(chatId: Int, senderId: Int, text: String): Message {
        chats[chatId]?.takeUnless { it.isDeleted } ?: throw ChatNotFoundException("Чат не найден или удален")
        val newMessage = Message(id = nextMessageId, text = text)
        return newMessage.also { addMessage(chatId, it) }
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
    fun addMessage(chatId: Int, message: Message) {
        val chat = chats[chatId] ?: throw ChatNotFoundException("Чат не найден")
        chat.messages += message
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

    val chatId2 = ChatService.createChat(1, 2)
    println("Создан чат с ID: $chatId2")

    val message1 = ChatService.createMessage(chatId2, 1, "Привет!")
    println("Сообщение отправлено: $message1")

    try {
        ChatService.editMessage(chatId2, message1.id, "Привет! Как дела?")
        println("Сообщение ${message1.id} успешно отредактировано")
    } catch (e: ChatNotFoundException) {
        println("Ошибка при редактировании сообщения: ${e.message}")
    }

    try {
        ChatService.editMessage(chatId2, message1.id, "Привет! Как дела?")
        println("Сообщение ${message1.id} успешно отредактировано")
    } catch (e: MessageNotFoundException) {
        println("Ошибка при редактировании сообщения: ${e.message}")
    }

    val userChats = ChatService.getChats(1)
    println("Список чатов для пользователя 1: $userChats")

    try {
        ChatService.deleteMessage(chatId2, message1.id)
        println("Сообщение ${message1.id} успешно удалено")
    } catch (e: ChatNotFoundException) {
        println("Ошибка при удалении сообщения: ${e.message}")
    }

    val lastMessages = ChatService.getLastMessagesFromChats()
    println("Последние сообщения из всех чатов: $lastMessages")

    try {
        val chatId3 = ChatService.createChat(1,3)
        val message1 = ChatService.createMessage(chatId3, 1, "Test1")
        val message2 = ChatService.createMessage(chatId3, 2, "Test2")
        val chatMessages = ChatService.getMessagesFromChat(chatId3, 10)
        println("Сообщения из чата: $chatMessages")

        val unreadCount = ChatService.getUnreadChatsCount()
        println("Количество непрочитанных чатов: $unreadCount")
        ChatService.deleteChat(chatId3)
    } catch (e: ChatNotFoundException) {
        println("Ошибка при получении сообщений: ${e.message}")
    }
    println("Последние сообщения из всех чатов: ${ChatService.getLastMessagesFromChats()}")

    try {
        ChatService.deleteChat(chatId1)
        println("Чат $chatId1 успешно удален")
    } catch (e: ChatNotFoundException) {
        println("Ошибка при удалении чата: ${e.message}")
    }

}