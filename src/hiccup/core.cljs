(ns hiccup.core)

(defn- concat-children
  ([children] (concat-children children []))
  ([[child & children] acc]
   (if (some? child)
     (if (= :<> (first child))
       (concat-children (concat (rest child) children) acc)
       (concat-children children (conj acc child)))
     acc)))

(defn reconcile [[keyword props & children]]
  (let [is-map? (map? props)
        all-children (if is-map? children (cons props children))]
    [(name keyword)
     (if is-map? props {})
     (concat-children all-children)]))

(defn tag-name [[key]]
  (name key))

(defn props [[_ p]]
  (if (map? p) p {}))

(defn children [[_ p & children]]
  (let [all-children (if (map? p) children (cons p children))]
    (concat-children all-children)))
