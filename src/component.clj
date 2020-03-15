(ns component)

(defn- generate-component-constructor [name]
  `(defn ~name []
     (js/Reflect.construct js/HTMLElement (cljs.core/clj->js []) ~name)))

(defn- state-renderer [attributes content]
  `(fn []
     (cljs.core/this-as this#
       (let [~'set-attribute (component/attributes-setter this#)
             hiccup# (~content @~attributes)]
         (component/do-the-render this# hiccup#)))))

(defn generate-attributes-names [{:keys [props] :or {props []}}]
  (mapv str props))

(defn- generate-component-properties [component-name name props]
  (let [attributes-sym (gensym 'attributes)]
    `(let [~attributes-sym (atom {})
           attributes-changed# (component/state-attributes-changed ~attributes-sym)
           render# ~(state-renderer attributes-sym (:render props))
           component-prototype# (js/Object.create (.-prototype js/HTMLElement))
           lifecycles# (cljs.core/clj->js
                        {"attributeChangedCallback" {:value attributes-changed#}
                         "connectedCallback" {:value component/connected-callback}
                         "render" {:value render#}})]
       (js/Object.defineProperties component-prototype# lifecycles#)
       (set! (.-prototype ~name) component-prototype#)
       (set! (.-observedAttributes ~name) (cljs.core/clj->js
                                           ~(generate-attributes-names props)))
       (.define js/customElements ~(str component-name) ~name))))

(defn- generate-attribute-setter [attribute]
  `(.setAttribute ~(str attribute) ~attribute))

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

(defmacro defcomponent
  ([name props] (generate-component name props))
  ([name args & body]
   (let [props {:props args
                :render `(fn [{:keys ~args}] ~@body)}]
     (generate-component name props))))
