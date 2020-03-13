(ns target-compile)

(defcomponent awesome-counter [value]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-state :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-state :value (- value 1))} "-"]]])

;; Should turn into:

(defn awesome-counter-constructor []
  (js/Reflect.construct js/HTMLElement (clj->js []) awesome-counter-constructor))

(defmacro state-renderer [state content]
  (fn []
    (this-as this
             (let [{:keys [value]}
                   set-state (fn [key value]
                               (swap! state (fn [st] (assoc st key value)))
                               (.render this))]
               (do-the-render this content)))))

(defn state-attributes-changed [state]
  (fn [name old-value new-value]
    (this-as this
             (swap! state #(assoc % (keyword name) new-value))
             (.render this))))

(defn connected-callback []
  (this-as this
           (.attachShadow this #js {:mode "open"})
           (.render this)))

(let [state (atom {:value nil})
      attributes-changed (state-attributes-changed state)
      render (state-renderer state
                             [:div "Hello world! This is an awesome counter!"
                              [:div
                               [:button {:on-click #(set-state :value (+ value 1))} "+"]
                               [:span (str value)]
                               [:button {:on-click #(set-state :value (- value 1))} "-"]]])
      component-prototype (js/Object.create (.-prototype js/HTMLElement))
      lifecycles (clj->js {"attributeChangedCallback" {:value (attributes-changed state)}
                           "connectedCallback" {:value connected-callback}
                           "render" {:value (render state)}})]
  (js/Object.defineProperties component-prototype lifecycles)
  (set! (.-prototype awesome-counter-constructor) component-prototype)
  (set! (.-observedAttributes awesome-counter-constructor) (clj->js ["value"]))
  (.define js/customElements "awesome-counter" awesome-counter-constructor))

(defn awesome-counter [{:keys [value]}]
  (doto object (js/document.createElement "awesome-counter")
        (.setAttribute "value" value)))
