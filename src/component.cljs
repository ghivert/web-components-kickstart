(ns component
  (:require [clojure.string :as string]))

(defn connected-callback []
  (this-as this
           (.attachShadow this (clj->js {:mode "open"}))
           (.render this)))

(defn select-value [value]
  (let [try-number (js/Number value)]
    (if (js/isNaN try-number)
      value
      try-number)))

(defn state-attributes-changed [state]
  (fn [name old-value new-value]
    (this-as this
             (let [value (select-value new-value)]
               (swap! state (fn [st] (assoc st (keyword name) value)))
               (.render this)))))

(defn extract-informations [[first args & children]]
  (if (map? args)
    [first args children]
    [first {} (cons args children)]))

(defn listener-type [string-attribute]
  (-> string-attribute
      (string/split #"-")
      rest
      string/join))

(defn add-attributes [paint args]
  (mapv (fn [[attribute value]]
          (let [string-attribute (name attribute)]
            (if (string/starts-with? string-attribute "on")
              (.addEventListener paint (listener-type string-attribute) value)
              (.setAttribute paint (name attribute) value))))
        args))

(declare paint-children)

(defn add-children [node children]
  (->> children
       (mapv paint-children)
       (mapv #(.appendChild node %))))

(defn paint-children [hiccup]
  (if (string? hiccup)
    (js/document.createTextNode hiccup)
    (let [[first args children] (extract-informations hiccup)
          paint (js/document.createElement (name first))]
      (add-attributes paint args)
      (add-children paint children)
      paint)))

(defn do-the-render [root hiccup]
  (let [node (.-shadowRoot root)]
    (doseq [child (array-seq (.-children node))]
      (.removeChild node child))
    (.appendChild node (paint-children hiccup))))
