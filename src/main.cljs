(ns main
  (:require [component :refer-macros [defcomponent]]))

(defcomponent awesome-counter [value]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-state :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-state :value (- value 1))} "-"]]])

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
      (.appendChild (awesome-counter {:value 0}))))

(defn reload! []
  (println "Reload"))
