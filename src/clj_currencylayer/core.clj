(ns clj-currencylayer.core
  (:require
    [clojure.string :refer [blank? join]]
    [cemerick.url :as url]
    [clj-http.client :as http]
    [io.aviso.toolchest.collections :refer [update-if?]]))


(def comma-join (partial join ","))

(def http-opts
  {:accept :json
   :content-type :json
   :throw-exceptions false})


(def routes
  (reduce-kv #(assoc %1 %2 (str "/api/" %3)) {} {:live "live"}))


(defn- unmarshal-response
  "Unmashals a `response` as JSON. Because currencylayer doesn't care about HTTP codes success and failure calls are
   returned with status code 200. It returns a tuple within following structure:
   `[:keyword parsed-body original-response]`.

   A `:keyword` can be:
   - :ok when a response is a success and parsed
   - :error-XXX when a response is parsed but it's an error response (where XXX is an error code from
     https://currencylayer.com/documentation#error_codes)
   - :error-unmarshalling when a response is not a valid JSON"
  [response]
  (try
    (let [body (http/json-decode (:body response) true)]
      (if (false? (:success body))
        [(keyword (str "error-" (-> body :error :code))) body response]
        [:ok body response]))
    (catch Exception e
      [:error-unmarshalling nil response])))


(defn- get-request
  [host path params]
  {:pre [(not (blank? host))]}
  (-> host
      str
      url/url
      (assoc :path path)
      (assoc :query params)
      str
      (http/get http-opts)
      unmarshal-response))


(def currencylayer-domain "apilayer.net")
(def currencylayer-host (str "https://" currencylayer-domain))
(def currencylayer-host-http (str "http://" currencylayer-domain))


(defn get-live
  "Returns the most recent exchange rate data. Pass all parameters via `params` hash-map.
   For API details see https://currencylayer.com/documentation

   Parameter `:currencies` is joined automatically, thus you can pass a sequece, e.g.: `[\"EUR\" \"CZK\"]`

   It returns a tuple like `[:keyword parsed-body original-response]`

   A `:keyword` can be:
   - :ok when a response is a success and parsed
   - :error-XXX when a response is parsed but it's an error response (where XXX is an error code from
     https://currencylayer.com/documentation#error_codes)
   - :error-unmarshalling when a response is not a valid JSON"
  ([params]
   (get-live params currencylayer-host))
  ([params host]
   (get-request host (:live routes) (update-if? params :currencies comma-join))))
