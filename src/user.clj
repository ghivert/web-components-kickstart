(ns user
  (:require [shadow.cljs.devtools.api :as shadow]))

(defn connect-to-app! []
  (shadow/nrepl-select :app))

(defn reset []
  (println "Not resetting, skipping…")
  (println "Connect to ClojurScript REPL."))
