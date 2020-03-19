(ns hiccup.vdom
  (:require [clojure.string :refer [lower-case starts-with?]]
            [hiccup.core :as hiccup]))

(defn isolate-props-events [props]
  (split-with (fn [param]
                (starts-with? (name (first param)) "on"))
              props))

(defn intersect-props [node props]
  (let [attrs (.-attributes node)]
    [{} {} {}]))

(defn update-node-props [node all-props]
  (let [[props events] (isolate-props-events all-props)
        [to-add to-update to-delete] (intersect-props node props)]
    node))

(defn diff-children [node children])

(defn create-new-node [name props children]
  (doto (js/document.createElement name)
        (update-node-props props)
        (diff-children children)))

(defn patch [node vec-dom]
  (let [[name props children] (hiccup/reconcile vec-dom)]
    (if (nil? node)
      (create-new-node name props children)
      (doto node
            (update-node-props props)
            (diff-children children)))))

(defn diff [root vec-dom]
  (println vec-dom)
  (js/console.log root)
  (let [el-name (hiccup/tag-name vec-dom)
        tag-name (lower-case (.-tagName root))]
    (if (= el-name tag-name)
      (patch root vec-dom)
      (.replaceChild (.-parentNode root) (patch nil vec-dom) root))))
