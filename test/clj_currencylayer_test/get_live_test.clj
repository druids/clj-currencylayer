(ns clj-currencylayer-test.get-live-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-currencylayer.core :as currencylayer]
    [clj-currencylayer-test.fake-http :as fake-http]))


(def currencylayer-host "https://api.currencylayer.localhost")

(def json-handler (partial fake-http/json-handler currencylayer-host))


(defn- assert-live-response
  [status body [actual-status actual-body actual-response]]
  (is (= status actual-status))
  (is (= body actual-body))
  (is (= 200 (:status actual-response))))


(deftest get-live-test
  (let [expected-url (str (:live currencylayer/routes) "?access_key=foo&currencies=CZK%2CEUR")
        success-response {:success true
                          :terms "https://currencylayer.com/terms"
                          :privacy "https://currencylayer.com/privacy"
                          :timestamp 1521448156
                          :source "USD"
                          :quotes {:USDCZK 20.7104, :USDEUR 0.814495}}
        error-response {:success false
                        :error {:code 105
                                :info (str "Access Restricted - Your current Subscription Plan does not support"
                                           " Source Currency Switching.")}}]
    (testing "valid response"
      (are [status body]
           (with-fake-routes (json-handler expected-url body)
             (assert-live-response status
                                   body
                                   (currencylayer/get-live {:access_key "foo", :currencies ["CZK" "EUR"]}
                                                           currencylayer-host)))

           :ok success-response
           :error-105 error-response))

    (testing "default host, with options"
      (with-fake-routes (fake-http/json-handler currencylayer/currencylayer-host expected-url success-response)
        (assert-live-response :ok
                              success-response
                              (currencylayer/get-live {:access_key "foo", :currencies ["CZK" "EUR"]})))))

  (testing "invalid response"
    (with-fake-routes {(str currencylayer-host (:live currencylayer/routes) "?access_key=foo")
                       {:get (fn [_] {:status 200, :body "{asassas"})}}
      (assert-live-response :error-unmarshalling nil (currencylayer/get-live {:access_key "foo"} currencylayer-host)))))
