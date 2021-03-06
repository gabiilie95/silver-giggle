package com.ilieinc.kotlinevents

class Event<T : IEvent>(eventType: Class<out T>) {
    private val type: Class<out T> = eventType
    private val subscribers = mutableListOf<T>()

    infix fun addSubscriber(subscriber: T) {
        subscribers.add(subscriber)
    }

    infix fun removeSubscriber(subscriber: T) {
        subscribers.remove(subscriber)
    }

    fun invoke(vararg args: Any) {
        val methods = type.declaredMethods
        subscribers.forEach {
            methods.forEach { method ->
                val classMethod = it.javaClass.getMethod(method.name, *method.parameterTypes)
                classMethod.invoke(
                    it,
                    *args
                )
            }
        }
    }
}