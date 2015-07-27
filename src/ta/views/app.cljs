(ns ta.views.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.calendar :refer [calendar-panel]]
            [ta.views.planner :refer [planner-panel]]
            [ta.views.classes :refer [classes-panel]]
            [ta.views.common :refer [sem icon flag-img]]
            [re-frame.core :refer [subscribe]]
            [shodan.inspection :refer [inspect]]))

(def page-links [{:key :timetable
                  :icon "calendar"
                  :label "Timetable"
                  :url "#/timetable"}
                 {:key :planner
                  :icon "book"
                  :label "Planbook"
                  :url "#/planner"}
                 {:key :classes
                  :icon "table"
                  :label "Classes"
                  :url "#/classes"}])

(defn nav-links [current-page]
  (let [active-page @current-page]
    (map (fn [link]
           (let [class (sem (if (= (:key link) active-page) "active") "item")
                 icon [:span {:style #js {:paddingRight 4}} (icon (:icon link))]
                 label (:label link)
                 url (:url link)]
             ^{:key label} [:a {:class class :href url} icon label]))
         page-links)))

(defn top-bar [active-page]
  (let [user (subscribe [:user])
        name (reaction (:name @user))
        flag (reaction (:flag @user))]
    (fn []
        [:div {:class "row"}
          [:div {:class "column"}
            [:div {:class "ui secondary pointing menu"}
              (nav-links active-page)
              [:div {:class "right menu"}
                [:a {:class "ui item"}
                  @name (flag-img @flag) (icon "caret down")]]]]])))

(defn main-panel [active-page]
  (let []
    (fn []
      [:div {:class "row"}
        [:div {:class "column"}
          (case @active-page :timetable [calendar-panel]
                             :planner   [planner-panel]
                             :classes   [classes-panel]
                             [:span "No Panel Found?"])]])))

(defn app-container []
  (let [active-page (subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 10}}
        [top-bar active-page]
        [main-panel active-page]])))
