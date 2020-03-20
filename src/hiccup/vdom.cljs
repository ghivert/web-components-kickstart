(ns hiccup.vdom
  (:require [clojure.string :refer [lower-case starts-with?]]
            [hiccup.core :as hiccup]))

(declare diff patch)

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

(defn remove-remaining-children [children]
  (doseq [child children]
    (.removeChild (.-parentNode child) child)))

(defn diff-children
  ([node children] (diff-children node (array-seq (.-childNodes node)) children))
  ([node [first-existing & rest-existing] [first-child & rest-child]]
   (if (some? first-child)
     (do
       (if (some? first-existing)
         (diff first-existing first-child)
         (.appendChild node (patch nil first-child)))
       (diff-children node rest-existing rest-child))
     (remove-remaining-children (concat first-existing rest-existing)))))

(defn select-node [node name]
  (if (nil? node)
    (js/document.createElement name)
    node))

(defn patch [node vec-dom]
  (if (string? vec-dom)
    (js/document.createTextNode vec-dom)
    (let [[name props children] (hiccup/reconcile vec-dom)
          to-patch (select-node node name)]
      (doto to-patch
            (update-node-props props)
            (diff-children children)))))

(defn diff [root vec-dom]
  (let [text-root? (= (.-nodeType root) (.-TEXT_NODE js/Node))]
    (if (and text-root? (string? vec-dom))
      (when-not (= (.-nodeValue root) vec-dom)
        (set! (.-nodeValue root) vec-dom))
      (let [el-name (hiccup/tag-name vec-dom)
            tag-name (lower-case (or (.-tagName root) ""))]
        (if (= el-name tag-name)
          (patch root vec-dom)
          (.replaceChild (.-parentNode root) (patch nil vec-dom) root))))))
