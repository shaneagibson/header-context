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