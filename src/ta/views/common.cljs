(ns ta.views.common
  (:require [shodan.inspection :refer [inspect]]
            [clojure.string :as string]
            [re-frame.core :refer [dispatch]]))

;; HELPERS

(defn sem
  "Returns a space separated string for use as an HTML component's :class"
  [& parts]
  (string/join " " parts))

(defn e->val
  "Take an input component's :on-change event and returns its target value"
  [event]
  (-> event .-target .-value))

;; COMPONENTS

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

(defn flag
  "Takes a country keyword and returns a flag icon element"
  [country]
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]
    [:div {:class "ui active small inline loader"}]))

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

#_(defn dropdown
  []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])

(defn dimmer
  [{:keys [active?]}]
  [:div {:class (sem "ui dimmer #_modals page #_transition #_visible"
                     (if active? "active"))}])

(defn modal
  [{:keys [active? type] :as arg-map}]
  (let [do-and-close (fn [thing-to-do] (thing-to-do)
                                       (dispatch [:modal :close]))]
    [:div {:class (sem "ui modal transition #_active"
                       (if active? "visible"))
           :style {:top "10%"}}
      [:i {:class "close icon"
           :on-click #(dispatch [:modal :close])}]
      (case type
        :confirm [:div {:class "header"} "Confirm"]
        [:div {:class "header"} (:header arg-map)])
      (case type
        :scrollbox [:div {:class "content"}
                     [:div {:style {:overflow-y "auto"
                                    :max-height "500px"}} (:content arg-map)]]
        :confirm   [:div {:class "image content"}
                     [:div {:class "image"} (icon (:i arg-map))]
                     [:div {:class "description"}
                       [:p (:question arg-map)]]]
        [:div {:class "content"} (get arg-map :content)])
      (case type
        :confirm [:div {:class "actions"}
                   [:div {:class "ui button"
                          :on-click #(do-and-close (:on-yes arg-map))}
                     "Yes, I'm sure"]]
        :form    [:div {:class "actions"}
                   [:div {:class "ui green button"
                          :on-click #(do-and-close (:on-submit arg-map))}
                     (:submit-text arg-map)]]
        nil)]))
