(ns hiccup.core)

(defn- concat-children
  ([children] (concat-children children []))
  ([[child & children] acc]
   (if (some? child)
     (if (= :<> (first child))
       (concat-children (concat (rest child) children) acc)
       (concat-children children (conj acc child)))
     acc)))

(defn reconcile [vec-dom]
  (if (string? vec-dom)
    ["" {} []]
    (let [[keyword props & children] vec-dom
          is-map? (map? props)
          all-children (if is-map? children (cons props children))]
      [(name keyword)
       (if is-map? props {})
       (concat-children all-children)])))

(defn tag-name [vec-dom]
  (if (string? vec-dom)
    ""
    (name (first vec-dom))))

(defn props [vec-dom]
  (if (string? vec-dom)
    {}
    (let [[_ p] vec-dom]
      (if (map? p) p {}))))

(defn children [vec-dom]
  (if (string? vec-dom)
    []
    (let [[_ p & children] vec-dom
          all-children (if (map? p) children (cons p children))]
      (concat-children all-children))))
