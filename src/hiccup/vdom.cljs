(ns hiccup.vdom
  (:require [clojure.string :refer [lower-case starts-with?]]
            [hiccup.core :as hiccup]))

(declare diff patch)

(defn isolate-props-events [props]
  (let [splitter (fn [[param]]
                   (if (starts-with? (name param) "on") :events :props))]
    (group-by splitter props)))

(defn reduce-events-props [props]
  (println "I'm in props" props)
  (fn [[update delete] [attr-name attr-val]]
    (let [prop (get props (keyword attr-name))]
      (if (some? prop)
        (if (js/Object.is attr-val prop)
          [update delete]
          [(assoc update attr-name prop) delete])
        [update (cons attr-name delete)]))))

(defn retain-events-props-to-add [props update delete]
  (println "retain-events-props-to-add:" props update delete)
  (filter (fn [[key value]]
            (println "Key!!!" key)
            (let [entry (name key)]
              (not (or
                    (contains? update entry)
                    (contains? delete entry)))))
          props))

(defn intersect-props [node props]
  (let [attrs (js->clj (js/Object.values (.-attributes node)))
        init [{} []]
        normalized-attrs (map (fn [val] [(.-name val) (.-nodeValue val)]) attrs)
        [update delete] (reduce (reduce-events-props props) init normalized-attrs)
        add (retain-events-props-to-add props update delete)]
    [add update delete]))

(defn intersect-events [node events]
  (let [evts (or (.-__events node) [])
        init [{} []]
        [update delete] (reduce (reduce-events-props events) init evts)
        add (retain-events-props-to-add events update delete)]
    [add update delete]))

(defn update-all-props [node [props-add props-update props-delete]])

(defn update-all-events [node [events-add events-update events-delete]])

(defn update-node-props [node all-props]
  (let [{:keys [events props]} (isolate-props-events all-props)
        final-props (intersect-props node props)
        final-events (intersect-events node events)]
    (println "All events" events)
    (println "All props" props)
    (doto node
          (update-all-props final-props)
          (update-all-events final-events))))

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
