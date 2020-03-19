(ns hiccup.core)

(defn reconcile [[keyword props & children]]
  (let [is-map? (map? props)]
    [(name keyword)
     (if is-map? props {})
     (if is-map? children (cons props children))]))

(defn tag-name [[key]]
  (name key))

(defn props [[_ p]]
  (if (map? p) p {}))

(defn children [[_ p & children]]
  (if (map? p) children (cons p children)))
