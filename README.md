# header-context

*header-context* maintains HTTP headers and makes them available for logging or auditing purposes, and for passing to subsequent requests.

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