(ns ta.views.common
  (:require [shodan.inspection :refer [inspect]]
            [clojure.string :as string]))

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

(defn flag-img [country]
  "Takes a country keyword and returns a flag icon element"
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]))

(defn test-dropdown []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])