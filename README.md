# header-context

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