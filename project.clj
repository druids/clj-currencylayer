(defproject clj-currencylayer "0.2.0"
  :description "A client for currencylayer.com API based on clj-http.client"
  :url "https://github.com/druids/clj-currencylayer"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/data.codec "0.1.1"]
                 [cheshire "5.8.0"]
                 [clj-http "3.8.0"]
                 [com.cemerick/url "0.1.1"]]

  :profiles {:dev {:plugins [[lein-cloverage "1.0.10"]
                             [lein-kibit "0.1.6"]]
                   :dependencies [[clj-http-fake "1.0.3"]
                                  [org.clojure/clojure "1.9.0"]]
                   :source-paths ["src" "dev/src"]}})