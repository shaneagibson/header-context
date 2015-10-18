# header-context

*header-context* is a Play Framework library for capturing HTTP headers. They can then be accessed for logging or auditing purposes, or for use in subsequent downstream requests.

Configure the ContextPropagatingDispatcher via:
```
akka {
    actor {
        default-dispatcher = {
            type = "monitoring.HeaderPropagatingDispatcherConfigurator"
        }
    }
}
```

Capture headers by invoking:
```
HeaderContext.captureHeaders(request.headers)
```

Retrieve headers by invoking:
```
HeaderContext.retrieveHeaders
```

> Based on some work by Yann Simon - http://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/