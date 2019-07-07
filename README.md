
Simple Event Facade for Java ... is to Event what flat log message is to Slf4J

Has bridge to enrich logback messages to send to elasticsearch extra applicative field-values.

10_000 foot overview:

```text

Chain using standard Slf4j Logging API:

 (slf4j)                (logback)                              (sef4j)
 Logger       -------->  ..LogbackAppende                      EventAppender
                         to LogingEventExt ---> enrich ----> 
                                                  add params mdc
                                                  add global prop "appName": "sample-app"
    .debug("msg")                    
    .info("msg test intVal:" + 1 + " strVal:" + str)
                  ---> log msg: INFO "msg test intVal:1 strVal:str"
                                                      ----->  poor event, NO params!
                                                             { 
                                                              "props": { "appName": "sample-app" },
                                                              "formattedMessage": "msg test..",
                                                              "params": {}
                                                             }
    .warn("warn msg")                


Chain using standard extended Logging API for name-value:
 (sef4j)
 LoggerExt
     .infoNV("msg test intVal:", 1, " strVal:", str)    
                 ---> log msg: INFO "msg test intVal:1 strVal:str"
                      (exactly SAME log message)
                                                      ---->  ENRICHED event !! with params
                                                            { 
                                                             "props": { "appName": "sample-app" },
                                                             "formattedMessage": "msg test..",
                                                             "params": {
                                                               "intVal": 1,
                                                               "strVal": "hello"
                                                             }
                                                            }


Chain using direct Event Api:
                                     (sef4j)         ----> 
                                     EventAppender         { type: "custom", 
                                     .sendEvent(..)          "intVal": 1, "strVal": "hello"
                                                           }
```


Code Example:
```java
    // send logs to logback 
    // ... also captured as LoggingEventExt in json (cf ElasticSearch)
    log.info("test");
    System.out.println();
    
    // send enriched logs with parameters in json (cf ElasticSearch)
    // ... also formatted to logback
    int intValue = 1;
    String strValue = "hello";
    logExt.infoNV("test intValue:", intValue, " strValue:", strValue);
    System.out.println();
    
    // direct send rich event in json (cf ElasticSearch)
    eventToJsonSysout.sendEvent(new DummyEvent(1, "abc"));
    System.out.println();
```

Result:
```
INFO  org.sef4j.sample.Sef4jSampleApp - test
event->json {"timeStamp":1562486183522,"level":"INFO","threadName":"main","loggerName":"org.sef4j.sample.Sef4jSampleApp","message":"test","argumentArray":null,"throwable":null,"callStackPath":null,
    "props":{"app":"sample-app"},
    "formattedMessage":"test",
    "params":{}
    }

INFO  org.sef4j.sample.Sef4jSampleApp - test intValue:1 strValue:hello
event->json {"timeStamp":1562486183680,"level":"INFO","threadName":"main","loggerName":"org.sef4j.sample.Sef4jSampleApp","message":"test intValue:{{intValue}} strValue:{{strValue}}","argumentArray":null,"throwable":null,"callStackPath":null,
    "props":{"app":"sample-app"},
    "formattedMessage":"test intValue:1 strValue:hello",
    "params":{"strValue":"hello","intValue":1}}

event->json {"intValue":1,"strValue":"abc"}

```


