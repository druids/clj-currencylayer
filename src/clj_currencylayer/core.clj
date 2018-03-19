(ns clj-currencylayer.core
  (:require
    [cemerick.url :as url]
    [clj-http.client :as http]))


(def http-opts
  {:accept :json
   :content-type :json
   :throw-exceptions false})
