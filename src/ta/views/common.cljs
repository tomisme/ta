(ns ta.views.common
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  (:require [cljs.test :refer-macros [is testing]]
            [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [clojure.string :as string]
            [ta.util :as util]))

(def size-strings
  {:mini "mini"
   :tiny "tiny"
   :small "small"
   :medium ""
   :large "large"
   :big "big"
   :huge "huge"
   :massive "massive"})

(defcard
  "#Let's make some reusable stuff!

   This is the namspace for components that are reused throughout the app.
   We're going to create some nice dynamic components using css from
   [Semantic UI](http://semantic-ui.com/) and stealing lots of functionality
   ideas from [re-com](https://github.com/Day8/re-com).")

(defn sem
  [& parts]
  (string/join " " parts))

(defn e->val
  ""
  [event]
  (-> event .-target .-value))

(deftest helper-tests
  "`sem` takes a sequence of strings and returns a space separated string.

   Useful as Semantic UI components are styled by applying multiple CSS classes, e.g.
   `<i class=\"ui labeled button\" >`."
   (is (= "ui labeled button" (sem "ui" "labeled" "button")))

   "`e->val` takes an input component's :on-change event and returns its target value"
   (is (= "green" (e->val "cheese"))))

(defcard
  "##Enough of these boring helpers, let's make some components!

   Each component can be passed a map of attributes. Some components can
   also handle a single argument as a shorthand for it's only mandatory attribute.")

(defn icon-el
  [arg]
  (if (map? arg)
    (let [{:keys [name color size]} arg
          color-str (get util/color-strings color)
          size-str  (get size-strings size)]
      [:i {:class (sem name color-str size-str "icon")}])
    [:i {:class (sem arg "icon")}]))

(defcard
  "`[icon-el \"red coffee\"]`"
  (dc/reagent [icon-el "red coffee"]))

(defcard
  "`[icon-el {:name \"home\" :color \"blue\" :size :big}]`"
  (dc/reagent [icon-el {:name "coffee"
                        :color :blue
                        :size :big}]))

(defn flag-el
  "Takes a country keyword and returns a flag icon element"
  [country]
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]
    [:div {:class "ui active small inline loader"}]))

(defn menu-el
  "Semantic UI menu element."
  [{:keys [items active-item on-change]}]
  [:div {:class "ui compact menu"}
    (for [{:keys [key label icon]} items]
      ^{:key label}
        [:a {:class (sem "item" (if (= active-item key) "active"))
             :on-click #(on-change key)}
         [:span {:style {:marginRight 5}} (icon-el icon)] label])])

(defn checkbox-el
  [on-click label checked]
  [:div {:class "ui checkbox"
         :onClick #(on-click (not checked))}
    [:input {:type "checkbox"
             :tabIndex "0"
             :class "hidden"
             :checked checked
             :readOnly true}]
    [:label label]])

(defn dropdown-el
  "Dropdown element, doesn't use semantic ui styling properly yet"
  [{:keys [value options starting on-change]}]
  [:select {:value value
            :onChange #(on-change %)}
    [:option {:value ""} starting]
    (map-indexed (fn [i option]
                   ^{:key (str i "-" option)}
                     [:option {:value option} option]) options)])

#_(defn semantic-ui-dropdown
  []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])

(defn input-el [{:keys [id name type placeholder on-blur val]}]
  "An input element which updates its value on change"
  ^{:key val} [:input {:id id
                       :name name
                       :placeholder placeholder
                       :class "form-control"
                       :type type
                       :default-value val
                       :on-blur on-blur}])
