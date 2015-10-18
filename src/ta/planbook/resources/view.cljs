(ns ta.planbook.resources.view
  (:require-macros [reagent.ratom :refer [reaction]]
                   [devcards.core :as dc :refer [defcard deftest]])
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [cljs.test :refer-macros [is testing]]
            [ta.common.remantic :refer [sem e->val icon-el input-el]]))

(def resource-types
  {:handout {:icon "file text outline" :label "Printed Handout"}
   :coffee  {:icon "coffee" :label "Coffee"}})

(defn resource-card
  [{:keys [name description type view]}]
  (let [type-icon (get-in resource-types [type :icon])
        type-str  (or (get-in resource-types [type :label]) "Teaching Resource")]
    [:div {:class "ui link card"}
     (if (= :large-card view)
       [:div {:class "image"}
        [:img {:style {:height 250}}]])
     [:div {:class "content"}
      [:div {:class "header"} name]
      [:div {:class "description"}
       (if type-icon [icon-el type-icon]) type-str]]]))

(defn resource-list
  [])

(defn resource-editor
  [])

(defn resources-tab
  []
  (let [selected (rf/subscribe [:open :resource])]
    [:div {:class "centered row"}
     [:div {:class (sem (if @selected "six" "sixteen") "wide column")}
      [resource-list]]
     (if @selected [resource-editor])]))

(defcard
  "Let's do things a little differently this time.

  All of our components should be as static as possible for easy testing with devcards.
  A wrapper component will pass down subscriptions and handlers.")

(def test-resource-data (rg/atom {:name "How to write a Haiku"
                                  :type :handout}))

(defcard resource-cards
  (dc/reagent
   (fn [data _]
     [:div {:class "ui two column grid"}
      [:div {:class "column"}
       ":large-card" [resource-card (assoc @data :view :large-card)]]
      [:div {:class "column"}
       ":small-card" [resource-card (assoc @data :view :small-card)]]]))
  test-resource-data
  {:inspect-data true})

(defcard resource-list)

(defcard resource-editor)
