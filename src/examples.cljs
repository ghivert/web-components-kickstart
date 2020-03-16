(ns examples
  (:require [web-component.core :refer-macros [defcomponent]]))

(defcomponent ^:shadow awesome-counter [value test]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(comment (defcomponent lifecycled-component
           {:on-enter (fn [state] (println "Enter"))
            :on-update (fn [state] (println "Update"))
            :on-exit (fn [state] (println "Out"))
            :props ["value"]
            :render (fn [state] [:div.class "Render"])
            :hook (fn [state] (swap! state
                                     #(assoc % :value (+ (:value %) 1))))}))
