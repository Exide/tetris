import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%date{E h:mm:ss.SSS} %logger{1}:%line [%level]: %msg\n"
    }
}

root(DEBUG, ["CONSOLE"])
