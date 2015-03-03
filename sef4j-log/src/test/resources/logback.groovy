import ch.qos.logback.core.*;
import ch.qos.logback.classic.*;

appender("FILE", ch.qos.logback.core.FileAppender) {
    file = "testFile.log"
    append = true
    encoder(PatternLayoutEncoder) {
      pattern = "%level %logger - %msg%n"
    }
  }
  
root(Level.INFO, ["FILE"])