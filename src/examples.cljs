(ns examples
  (:require [web-component.core :refer-macros [defcomponent]]))

(defcomponent ^:shadow awesome-counter [value test]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

