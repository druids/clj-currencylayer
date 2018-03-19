clj-currencylayer
=================

A client for [currencylayer.com API](currencylayer.com) based on [clj-http.client](https://clojars.org/clj-http).

[![CircleCI](https://circleci.com/gh/druids/clj-currencylayer.svg?style=svg)](https://circleci.com/gh/druids/clj-currencylayer)
[![Dependencies Status](https://jarkeeper.com/druids/clj-currencylayer/status.png)](https://jarkeeper.com/druids/clj-currencylayer)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------

```clojure
[clj-currencylayer "0.0.0"]
```


Documentation
-------------

All functions are designed to return errors instead of throwing exceptions (except `:pre` in a function).

All API calls return a tuple within following structure: `[:keyword body response]` where`:keyword` can be:
- :ok when a response is a success and parsed
- :error-XXX when a response is parsed but it's an error response (where XXX is an error code from
  [error codes](https://currencylayer.com/documentation#error\_codes))
- :error-unmarshalling when a response is not a valid JSON

A `body` is a parsed body and `response` is an original response.

To be able to run examples this line is needed:

```clojure
(require '[clj-currencylayer.core :as currencylayer])
```

### get-live

Returns the most recent exchange rate data. Pass all parameters via `params` hash-map.

```clojure
(:currencylayer/get-live {:access_key "asdf"})
;; [:ok
;;  {:success true, :timestamp 1521448156, :source "USD", :quotes {:USDCZK 20.7104, :USDEUR 0.814495}, ...
;;  {:request-time 386, ...
```

`:currencies` are joined automatically, thus you can pass them as a sequence

```clojure
(:currencylayer/get-live {:access_key "asdf"} ["EUR" "CZK"])
```

Because of a tuple you can do kind of "pattern matching":

```clojure
(let [status body _]
  (case status
    :ok (process-currencies body)
    :error-101 (missing-auth-key body)
    (default-error body)))
```

When you need to mock a response, you can pass a host to a caller

```clojure
(currencylayer/get-live {:access_key "asdf"} "my-mock-domain.localhost")
```

Or for development you can use HTTP host

```clojure
(currencylayer/get-live {:access_key "asdf"} currencylayer/currencylayer-host-http)
```
