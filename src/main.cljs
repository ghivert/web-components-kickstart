(ns main
  (:require [web-component.core :as wc :refer-macros [defcomponent]]
            [examples]))

(defn main! []
  (println "Main")
  (let [app (js/document.getElementById "app")]
    (wc/render! app (examples/app-root))))

(defn reload! []
  (println "Reload"))
