(ns main
  (:require [web-component.core :refer-macros [defcomponent]]
            [examples]))

(defn main! []
  (println "Main")
  (doto (js/document.getElementById "app")
        (.appendChild (examples/awesome-counter {:value 0}))
        (.appendChild (examples/lifecycled-component {:value 0}))))

(defn reload! []
  (println "Reload"))
