appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%date{E h:mm:ss.SSS} %logger{1}:%line [%level]: %msg\n"
    }
}

root(INFO, ["CONSOLE"])
