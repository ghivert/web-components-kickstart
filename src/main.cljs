(ns main
  (:require [web-component.core :refer-macros [defcomponent]]
            [examples]))

(comment (defcomponent lifecycled-component
           {:on-enter (fn [state] (println "Enter"))
            :on-update (fn [state] (println "Update"))
            :on-exit (fn [state] (println "Out"))
            :props ["value"]
            :render (fn [state] [:div.class "Render"])
            :hook (fn [state] (swap! state
                                     #(assoc % :value (+ (:value %) 1))))}))

(defn main! []
  (println "Main")
  (-> (js/document.getElementById "app")
      (.appendChild (examples/awesome-counter {:value 0}))))

(defn reload! []
  (println "Reload"))
