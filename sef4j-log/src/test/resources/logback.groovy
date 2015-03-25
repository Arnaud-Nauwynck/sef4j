import org.sef4j.core.api.EventLogger;

import ch.qos.logback.core.*;
import ch.qos.logback.classic.*;

appender("STDOUT", ch.qos.logback.core.ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
      pattern = "%level %logger - %msg%n"
    }
}

appender("FILE", ch.qos.logback.core.FileAppender) {
    file = "testFile.log"
    append = true
    encoder(PatternLayoutEncoder) {
      pattern = "%date %level %logger - %msg%n"
    }
}

appender("eventSenderAppender", org.sef4j.log.slf4j.slf4j2event.EventSenderSlf4jAppender) {
    targetEventSender = new org.sef4j.core.helpers.senders.InMemoryEventSender();
}

root(Level.INFO, ["STDOUT", "eventSenderAppender"])
