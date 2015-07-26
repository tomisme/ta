(ns ta.views.common
  (:require [clojure.string :as string]))

(defn sem [& parts]
  "Returns a space separated string for use as an HTML component's :class"
  (string/join " " parts))

(defn e->val [e]
  "Take an input component's :on-change event and returns the its value"
  (-> e .-target .-value))

(defn ibut [value]
  "Handy inspection button! Click to inspect a value in the console"
  [:button {:on-click #(inspect value)} "what?"])

(defn icon
  "Takes name string and optional size keyword and returns an icon element"
  ([name] (icon name :m))
  ([name size]
    (let [size-str (case size
                         :s "small"
                         :m ""
                         :l "large")]
    [:i {:class (sem "icon" name size-str)}])))
