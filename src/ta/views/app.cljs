(ns ta.views.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.calendar :refer [calendar-panel]]
            [ta.views.planbook :refer [planbook-panel]]
            [ta.views.classes :refer [classes-panel]]
            [ta.views.common :refer [sem icon flag-img]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def page-links [{:key :calendar
                  :icon "calendar"
                  :label "Calendar"
                  :url "#/calendar"}
                 {:key :planbook
                  :icon "book"
                  :label "Planbook"
                  :url "#/planbook"}
                 {:key :classes
                  :icon "table"
                  :label "Classes"
                  :url "#/classes"}])

(defn nav-links [active-page]
  (let [current-page @active-page]
    (for [link page-links]
      (let [class (sem (if (= current-page (:key link)) "active") "item")
            icon [:span {:style #js {:paddingRight 4}} (icon (:icon link))]
            label (:label link)
            url (:url link)]
        ^{:key label} [:a {:class class :href url} icon label]))))

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
                @name (flag-img @flag) (icon "caret down")]
              [:div {:class "ui item"}
                [:div {:class "ui labeled icon button"
                       :onClick #(dispatch [:inspect-db])}
                  (icon "world") "db"]]]]]])))

(defn main-panel [active-page]
  [:div {:class "row"}
    [:div {:class "column"}
      (case @active-page :calendar [calendar-panel]
                         :planbook [planbook-panel]
                         :classes  [classes-panel]
                         [:span "No Panel Found?"])]])

(defn app-container []
  (let [active-page (subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 0}} ;; better way?
        [top-bar active-page]
        [main-panel active-page]])))
