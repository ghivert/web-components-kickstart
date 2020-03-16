# Web Components Kickstart

Web components are great. But you probably thought « Gosh, this seems too much complicated for me! Let's rest in re-frame! ». And you probably would be right. But we're in Lisp there, and we can extend the language as much as we want. So let's try adding Web Components easily!

# Getting Started

Get the code. Do how you want, because this package is, of course, still not published yet.

Oh, I almost forgot! Run Shadow CLJS. It's cool. It's the best ClojureScript experience you can expect right now.

# Using the package.

Ok, the hard part. Ready? Go.

```clojure
(ns my-awesome-app
  (:require [component :refer-macros [defcomponent]]))

(defcomponent my-awesome-component [value]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(defcomponent ^:shadow my-awesome-component [value]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(defcomponent ^{:shadow :closed} my-awesome-component [value]
  [:div "Hello world! This is an awesome counter!"
   [:div
    [:button {:on-click #(set-attribute :value (+ value 1))} "+"]
    [:span (str value)]
    [:button {:on-click #(set-attribute :value (- value 1))} "-"]]])

(defn -main []
  (-> (js/document.getElementById "app")
    (.appendChild (awesome-counter {:value 0}))))
```

You're done.

This will create a WebComponent, register it in the Custom Elements registry, and mount one in the DOM. Try it, it’s working!

Oh, of course you can use hiccup. That’s the best HTML representation no? 😉 Oh. And of course it runs a virtual DOM (well, I should say « It should run a virtual DOM », because it’s not yet implemented) because that’s the easiest way to run DOM diffing no?
