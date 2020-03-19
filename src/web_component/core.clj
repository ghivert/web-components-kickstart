(ns web-component.core)

(defn- generate-component-constructor [name]
  `(defn ~name []
     (js/Reflect.construct js/HTMLElement (cljs.core/clj->js []) ~name)))

(defn- state-renderer [root attributes content]
  `(fn []
     (cljs.core/this-as this#
       (let [~'set-attribute (web-component.core/attributes-setter this#)
             hiccup# (~content @~attributes)]
         (web-component.core/do-the-render ~root hiccup#)))))

(defn generate-attributes-names [{:keys [props] :or {props []}}]
  (mapv str props))

(defn- generate-component-properties [component-name name props]
  (let [attributes-sym (gensym 'attributes)
        root-sym (gensym 'root)]
    `(let [~attributes-sym (atom {})
           ~root-sym (atom nil)
           attributes-changed# (web-component.core/state-attributes-changed ~attributes-sym ~(:on-update props))
           render# ~(state-renderer root-sym attributes-sym (:render props))
           component-prototype# (js/Object.create (.-prototype js/HTMLElement))
           lifecycles# (cljs.core/clj->js
                        {"attributeChangedCallback" {:value attributes-changed#}
                         "connectedCallback" {:value (web-component.core/connected-callback ~root-sym ~(meta component-name) ~(:on-enter props))}
                         "render" {:value render#}
                         "disconnectedCallback" {:value (web-component.core/disconnected-callback ~(:on-exit props))}})]
       (js/Object.defineProperties component-prototype# lifecycles#)
       (set! (.-prototype ~name) component-prototype#)
       (set! (.-observedAttributes ~name) (cljs.core/clj->js
                                           ~(generate-attributes-names props)))
       (if (js/customElements.get ~(str component-name))
         (js/location.reload)
         (.define js/customElements ~(str component-name) ~name)))))

(defn- generate-attribute-setter [attribute]
  `(web-component.core/add-attribute ~(str attribute) ~attribute))

(defn- generate-component-builder [name props]
  (let [attributes (:props props)
        attributes-setters (mapv generate-attribute-setter attributes)]
    `(defn ~name [{:keys ~attributes}]
       (doto (js/document.createElement ~(str name))
             ~@attributes-setters))))

(defn- generate-component [name props]
  (let [constructor-name (gensym name)
        constructor (generate-component-constructor constructor-name)
        properties (generate-component-properties name constructor-name props)
        builder (generate-component-builder name props)]
    `(do
       ~constructor
       ~properties
       ~builder)))

(defn- string->symbol [string]
  `~(symbol string))

(defn- props->symbols [props]
  (mapv string->symbol props))

(defn- update-props-in-args [args]
  (assoc args :props (props->symbols (:props args))))

(defmacro defcomponent
  ([name args]
   (let [props (update-props-in-args args)]
     (generate-component name props)))
  ([name args & body]
   (let [props {:props args
                :render `(fn [{:keys ~args}] ~@body)}]
     (generate-component name props))))
