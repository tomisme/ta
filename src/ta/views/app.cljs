(ns ta.views.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [shodan.inspection :refer [inspect]]
            [ta.views.planbook.planbook :refer [planbook-view]]
            [ta.views.calendar :refer [calendar-view]]
            [ta.views.classes :refer [classes-view]]
            [ta.views.common :refer [sem icon flag]]
            [ta.views.modal :refer [global-modal dimmer]]
            [re-frame.core :refer [subscribe dispatch]]))

(defn top-bar
  [active-page]
  (let [user (subscribe [:user])
        pages {:calendar {:i "calendar"
                          :label "Calendar"
                          :url "#/calendar"}
               :planbook {:i "book"
                          :label "Planbook"
                          :url "#/planbook"}
               :classes  {:i "table"
                          :label "Classes"
                          :url "#/classes"}}]
    (fn []
      [:div {:class "one column row"}
        [:div {:class "column"}
          [:div {:class "ui secondary pointing menu"}
            (doall (for [[k {:keys [i label url]}] pages
                         :let [class (sem (if (= @active-page k) "active") "item")
                               icon [:span {:style #js {:paddingright 4}} (icon i)]]]
                     ^{:key label} [:a {:class class :href url} icon label]))
            [:div {:class "right menu"}
              [:a {:class "ui item"}
                (:name @user) (flag (:flag @user)) (icon "caret down")]
              [:div {:class "ui item"}
                [:div {:class "ui labeled icon button"
                       :onClick #(dispatch [:launch-db-modal])}
                  (icon "search") "db"]]]]]])))

(defn main-view
  [active-page]
  [:div {:class "one column row"}
    [:div {:class "column"}
      (case @active-page :calendar [calendar-view]
                         :planbook [planbook-view]
                         :classes  [classes-view]
                         [:span "No Page Found!"])]])

(defn app-container
  []
  (let [active-page (subscribe [:active-page])
        modal?      (subscribe [:modal :active?])]
    (fn []
      [:div {:class "ui grid container"
             :style #js {:margin 0}}
        [dimmer {:active? @modal?}]
        [global-modal]
        [top-bar active-page]
        [main-view active-page]])))
