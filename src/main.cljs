(ns main)

(defn attributes-changed [state]
  (fn [name old-value new-value]
    (this-as this
             (swap! state #(assoc % (keyword name) new-value))
             (.render this))))

(defn render [state]
  (fn []
    (this-as this
             (js/console.log this)
             (println (str "state is " @state))
             (println "Hello from rendering!"))))

(defn connected-callback []
  (this-as this
           (.attachShadow this #js {:mode "open"})))

(defn component []
  (js/Reflect.construct js/HTMLElement (clj->js []) component))

(let [component-prototype (js/Object.create (.-prototype js/HTMLElement))
      state (atom {})
      lifecycles (clj->js {"attributeChangedCallback" {:value (attributes-changed state)}
                           "connectedCallback" {:value connected-callback}
                           "render" {:value (render state)}})]
  (js/Object.defineProperties component-prototype lifecycles)
  (set! (.-prototype component) component-prototype)
  (set! (.-observedAttributes component) (clj->js ["country"]))
  (.define js/customElements "muuuuss-component" component))

(defn main! []
  (println "Main"))

(defn reload! []
  (println "Reload"))
