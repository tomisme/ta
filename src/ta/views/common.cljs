(ns ta.views.common
  (:require [shodan.inspection :refer [inspect]]
            [clojure.string :as string]))

(defn sem
  "Returns a space separated string for use as an HTML component's :class"
  [& parts]
  (string/join " " parts))

(defn e->val
  "Take an input component's :on-change event and returns its target value"
  [event]
  (-> event .-target .-value))

(defn ibut
  "Handy inspection button! Click to inspect a value in the console"
  [value]
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

(defn flag-img
  "Takes a country keyword and returns a flag icon element"
  [country]
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]))

(defn checkbox
  [on-click label checked]
  [:div {:class "ui checkbox"
         :onClick #(on-click (not checked))}
    [:input {:type "checkbox"
             :tabIndex "0"
             :class "hidden"
             :checked checked
             :readOnly true}]
    [:label label]])

(defn dropdown
  [{:keys [value options starting on-change]}]
  [:select {:value value
            :onChange #(on-change %)}
    [:option {:value ""} starting]
    (map-indexed (fn [i option]
                   ^{:key (str i "-" option)}
                     [:option {:value option} option]) options)])

(defn semantic-dropdown
  []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])
