(ns web-component.core
  (:require [clojure.string :as string]))

(defn- attach-shadow [root this mode]
  (if (nil? mode)
    (reset! root this)
    (let [shadow-root (.attachShadow this (clj->js {:mode mode}))]
      (reset! root shadow-root))))

(defn- add-shadow-root! [root this metadata]
  (let [{:keys [shadow] :or {shadow nil}} metadata]
    (case shadow
      :open   (attach-shadow root this "open")
      :closed (attach-shadow root this "closed")
      true    (attach-shadow root this "open")
      (attach-shadow root this nil))))

(defn- connected-callback [root metadata on-enter]
  (fn []
    (this-as this
      (add-shadow-root! root this metadata)
      (.render this)
      (when-not (nil? on-enter)
        (on-enter this)))))

(defn- disconnected-callback [on-exit]
  (fn []
    (this-as this
      (when-not (nil? on-exit)
        (on-exit this)))))

(defn- select-value [value]
  (let [try-number (js/Number value)]
    (if (js/isNaN try-number)
      value
      try-number)))

(defn- attributes-setter [this]
  (fn [key value]
    (.setAttribute this (name key) value)
    (.render this)))

(defn- state-attributes-changed [state on-update]
  (fn [name old-value new-value]
    (this-as this
      (let [value (select-value new-value)]
        (swap! state (fn [st] (assoc st (keyword name) value)))
        (.render this)
        (when-not (nil? on-update)
          (on-update this))))))

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

(declare paint-children)

(defn- add-children [node children]
  (->> children
       (mapv paint-children)
       (mapv #(.appendChild node %))))

(defn- paint-children [hiccup]
  (if (string? hiccup)
    (js/document.createTextNode hiccup)
    (let [[first args children] (extract-informations hiccup)
          paint (js/document.createElement (name first))]
      (add-attributes paint args)
      (add-children paint children)
      paint)))

(defn- do-the-render [root hiccup]
  (let [node @root]
    (when node
      (doseq [child (array-seq (.-children node))]
        (.removeChild node child))
      (.appendChild node (paint-children hiccup)))))
