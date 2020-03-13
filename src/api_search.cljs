(ns api-search)

(defcomponent stateless-component [value]
  [:div.title "I'm stateless"
   [:div.subtitle "You fool"]
   [:div value]])

(defcomponent stateful-component [value]
  [:div.title "I'm stateful"
   [:div.subtitle "You fool"]
   [:div value]
   [:button #(set-state :value (+ value 1))]])

(defcomponent lifecycled-component
  {:on-enter (fn [state] (println "Enter"))
   :on-update (fn [state] (println "Update"))
   :on-exit (fn [state] (println "Out"))
   :props [value]
   :render (fn [state] [:div.class "Render"])
   :hook (fn [state] (swap! state
                            #(assoc % :value (+ (:value %) 1))))})

(defn generation-function []
  [:div "Hello from classic, good-ol hiccup!"])

(defcomponent stateless-also []
  [:div.title "Here an example of using components and hiccup."
   [:stateful-component {:value 1}]
   [generation-function]
   [:stateless-component {:value "Static"}]])
