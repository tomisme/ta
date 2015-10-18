(ns ta.common.remantic
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

(deftest sem-test
  "`sem` takes a sequence of strings and returns a space separated string.

  Useful as Semantic UI components are styled by applying multiple CSS classes, e.g.
  `<i class=\"ui labeled button\">`."
  (is (= "ui labeled button" (sem "ui" "labeled" "button"))))

(def e->val-atom (atom {:event nil
                        :val nil}))

(defcard e->val-test
  "`e->val` takes an event emmitted by an HTML element (like an `<input>`) and returns
  the event target's value attribute"
  (dc/reagent [:input {:on-change #(swap! e->val-atom (fn [data]
                                                        (-> data
                                                          (assoc :event %)
                                                          (assoc :val (e->val %)))))}])
  e->val-atom
  {:inspect-data true})

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
  "```
  [icon-el \"red coffee\"]
  ```"
  (dc/reagent [icon-el "red coffee"]))

(defcard
  "```
  [icon-el {:name \"coffee\"
  :color \"blue\"
  :size :big}]
  ```"
  (dc/reagent [icon-el {:name "coffee"
                        :color :blue
                        :size :big}]))

(defn flag-el
  [arg]
  (if (map? arg)
    (flag-el (get arg :country))
    (case arg
      :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]
      [:div {:class "ui active small inline loader"}])))

(defcard
  "```
  [flag-el :australia]
  ```"
  (dc/reagent [flag-el :australia]))

(defcard
  "```
  [flag-el {:country :australia}]
  ```"
  (dc/reagent [flag-el {:country :australia}]))

(defn menu-el
  [{:keys [items active-item on-change]}]
  [:div {:class "ui compact menu"}
   (for [{:keys [key label icon]} items]
     ^{:key label}
     [:a {:class (sem "item" (if (= active-item key) "active"))
          :on-click #(on-change key)}
      [:span {:style {:marginRight 5}} (icon-el icon)] label])])

(defcard
  "```
  [menu-el {:items [{:key :home :label \"Home\" :icon \"home\"}
  {:key :plus :label \"Plus\" :icon \"plus\"}]
  :active-item :home
  :on-change (fn [new-key] (js/alert new-key))}]
  ```"
  (dc/reagent [menu-el {:items [{:key :home :label "Home" :icon "home"}
                                {:key :plus :label "Plus" :icon "plus"}]
                        :active-item :home
                        :on-change (fn [new-key] (js/alert new-key))}]))

(defn checkbox-el
  [{:keys [label checked on-change]}]
  [:div {:class "ui checkbox"
         :on-click #(on-change (not checked))}
   [:input {:type "checkbox"
            :tabIndex "0"
            :class "hidden"
            :checked checked
            :readOnly true}]
   [:label label]])

(defcard
  "```
  [checkbox-el {:label \"Hello\"
  :checked :true
  :on-change (fn [new-val] (js/alert new-val))}]
  ```"
  (dc/reagent [checkbox-el {:label "Hello"
                            :checked :true
                            :on-change (fn [new-val] (js/alert new-val))}]))

(defn dropdown-el
  [{:keys [value options starting on-change]}]
  [:select {:value value
            :on-change #(on-change %)}
   [:option {:value ""} starting]
   (map-indexed (fn [i option]
                  ^{:key (str i "-" option)}
                  [:option {:value option} option]) options)])

(defcard
  "Dropdown component needs to be wrapped in a 'ui form' div in order to be styled.
  ```
  [:div {:class \"ui form\"}
  [dropdown-el {:starting \"Simpson's Character\"
  :options [\"Bart\" \"Lisa\" \"Maggie\"]
  :value \"\"
  :on-change (fn [event] (js/alert (e->val event)))}]]
  ```"
  (dc/reagent [:div {:class "ui form"}
               [dropdown-el {:starting "Simpson's Character"
                             :options ["Bart" "Lisa" "Maggie"]
                             :value ""
                             :on-change (fn [event] (js/alert (e->val event)))}]]))

(defn semantic-ui-dropdown
  []
  [:div {:class "ui selection dropdown"}
   [:input {:type "hidden" :name "gender"}]
   [:div {:class "default text"} "Gender"]
   [:i {:class "dropdown icon"}]
   [:div {:class "menu"}
    [:div {:class "item" :data-value "1"} "Male"]
    [:div {:class "item" :data-value "0"} "Female"]]])

(dc/defcard-doc
  "TODO: Use proper Semantic UI css for dropdown component. Here's a start:"
  (dc/mkdn-pprint-source semantic-ui-dropdown))

(defn input-el
  [{:keys [id name type placeholder on-blur on-change val]}]
  [:input {:id id
           :name name
           :placeholder placeholder
           :type type
           :default-value val
           :on-blur on-blur
           :on-change on-change}])

(defcard
  "Input component needs to be wrapped in a 'ui form' div in order to be styled.
  ```
  [:div {:class \"ui form\"}
  [input-el {:placeholder \"Enter your name\"
  :type \"text\"
  :val \"Here's your money\"
  :on-blur (fn [event] (js/alert (e->val event)))}]]
  ```"
  (dc/reagent [:div {:class "ui form"}
               [input-el {:placeholder "Enter your name"
                          :type "text"
                          :val "Here's your money"
                          :on-blur (fn [event] (js/alert (e->val event)))}]]))
