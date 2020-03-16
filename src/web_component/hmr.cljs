(ns web-component.hmr)

(defn generate-define [registry-define]
  (let [components-cache (atom {})]
    (fn [name constructor options]
      (this-as this
        (let [key-name (keyword name)
              component-constructor (key-name @components-cache)]
          (if-not component-constructor
            (do
              (swap! components-cache #(assoc % key-name constructor))
              (.call registry-define this name constructor))))))))

(defn patch [registry]
  (let [registry-define (-> registry .-prototype .-define)
        new-define (generate-define registry-define)]
    (set! (-> registry .-prototype .-define) new-define)))

(defn apply-patch []
  (patch js/CustomElementRegistry))
