(ns examples
  (:require [web-component.core :refer-macros [defcomponent]]))

(defcomponent ^:shadow awesome-counter [value test]
  [:div "Hello world! This is an awesome counter!"
   [:p "I'm so awesome"]]
  [:<>
   [:<>
    [:button {:class "test" :value 12 :on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(defcomponent lifecycled-component
  {:props ["value"]
   :render (fn [{:keys [value]}]
             [:div "Hello world! This is a lifecycle counter!"
              [:div
               [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
               [:span (str value)]
               [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])})

(defcomponent ^:shadow app-root []
  [:awesome-counter {:value 0 :test true}]
  [:lifecycled-component {:value 64}])
