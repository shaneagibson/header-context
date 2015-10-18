# header-context

*header-context* is a microservice-friendly Play Framework library for capturing HTTP headers. They can then be accessed
for logging or auditing purposes, or for use in subsequent downstream requests.

Configure the ContextPropagatingDispatcher via:
```
akka {
    actor {
        default-dispatcher = {
            type = "uk.co.epsilontechnologies.headercarrier.HeaderPropagatingDispatcherConfigurator"
        }
    }
}
```
> Based on some work by Yann Simon - http://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/

### Usage

Capture headers by invoking:
```
HeaderContext.captureHeaders(request.headers)
```

Retrieve headers by invoking:
```
HeaderContext.retrieveHeaders
```

### Recommendations

Use of ThreadLocal in an asynchronous application is not ideal. Context should really be passed and maintained
explicitly. Ultimately, if your application requires a lot of state to be maintained
and subsequently passed downstream via request headers, you might want to reconsider your solution.