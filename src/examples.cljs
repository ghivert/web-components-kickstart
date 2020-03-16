(ns examples
  (:require [web-component.core :refer-macros [defcomponent]]))

(defcomponent ^:shadow awesome-counter [value test]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(defcomponent lifecycled-component
  {:on-enter (fn [this] (println "Enter"))
   :on-update (fn [this] (println "Update"))
   :on-exit (fn [this] (println "Out"))
   :props ["value"]
   :render (fn [{:keys [value]}]
             [:div "Hello world! This is an awesome counter!"
              [:div
               [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
               [:span (str value)]
               [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])
   :hook (fn [state] (swap! state
                            #(assoc % :value (+ (:value %) 1))))})
