package me.fireandice.ghosttracker.utils

import me.fireandice.ghosttracker.GhostTracker
import org.apache.logging.log4j.message.AbstractMessageFactory
import org.apache.logging.log4j.message.FormattedMessage
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.ParameterizedMessage

class GhostTrackerMessageFactory : AbstractMessageFactory() {

    override fun newMessage(message: String?, vararg params: Any?): Message {
        val parameterizedMessage = ParameterizedMessage(message, params)
        return FormattedMessage("[${GhostTracker.NAME}%s] %s", "/${GhostTracker.NAME}", parameterizedMessage)
    }
}