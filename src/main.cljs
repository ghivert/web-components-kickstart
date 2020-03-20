(ns main
  (:require [web-component.core :as wc :refer-macros [defcomponent]]
            [examples]))

(def app-state (atom {:user nil}))

(def ui)

(defn main! []
  (println "Main")
  (let [app (js/document.getElementById "app")]
    (wc/render! app (examples/awesome-counter {:value 0}))))

(defn reload! []
  (println "Reload")
  (let [node (js/document.querySelector "#app > awesome-counter")]
    (.setAttribute node "value" (+ 1 (js/parseInt (.-value node))))))
