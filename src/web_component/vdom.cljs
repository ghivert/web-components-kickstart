(ns web-component.vdom
  (:require [clojure.string :as string]
            [hiccup.vdom]
            [hiccup.core]))

(declare paint-children)

(defn- add-children [node children]
  (->> children
       (mapv paint-children)
       (mapv #(.appendChild node %))))

(defn- extract-informations [[first args & children]]
  (if (map? args)
    [first args children]
    [first {} (cons args children)]))

(defn- listener-type [string-attribute]
  (-> string-attribute
      (string/split #"-")
      rest
      string/join))

(defn- add-attributes [paint args]
  (mapv (fn [[attribute value]]
          (let [string-attribute (name attribute)]
            (if (string/starts-with? string-attribute "on")
              (.addEventListener paint (listener-type string-attribute) value)
              (.setAttribute paint (name attribute) value))))
        args))

(defn- paint-children [hiccup]
  (if (string? hiccup)
    (js/document.createTextNode hiccup)
    (let [[first args children] (extract-informations hiccup)
          paint (js/document.createElement (name first))]
      (add-attributes paint args)
      (add-children paint children)
      paint)))

(defn render [root hiccup]
  ;; root is an atom because it is required to use it with Shadow DOM.
  (let [node @root]
    (when node
      (if (vector? hiccup)
        (hiccup.vdom/diff-children node (hiccup.core/concat-children hiccup))
        (do
          (println "Your render is not a valid Hiccup.")
          (println hiccup))))))
