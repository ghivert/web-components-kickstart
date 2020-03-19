(ns web-component.core
  (:require [clojure.string :as string]
            [web-component.vdom]))

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

(defn- constructor-add-attribute [node name value]
  (when-not (nil? value)
    (if (boolean? value)
      (if value
        (.setAttribute node name "")
        (.removeAttribute node name))
      (.setAttribute node name value))))

(defn render! [node & children]
  (doseq [child children]
    (.appendChild node child)))
