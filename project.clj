(defproject timscript "0.1.0-SNAPSHOT"
  :description "A Music DSL"
  :url "http://github.com/shannonjanehogan/timscript"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot timscript.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
