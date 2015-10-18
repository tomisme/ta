(ns ta.view
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [ta.common.remantic :refer [sem icon-el flag-el]]
            [ta.common.modal :refer [global-modal dimmer]]
            [ta.planbook.view :refer [planbook-view]]
            [ta.calendar.view :refer [calendar-view]]
            [ta.classes.view :refer [classes-view]]))

(defn top-bar
  [active-page]
  (let [user (rf/subscribe [:user])
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
                            icon [:span {:style #js {:paddingright 4}} (icon-el i)]]]
                  ^{:key label} [:a {:class class :href url} icon label]))
         [:div {:class "right menu"}
          [:a {:class "ui item"}
           (:name @user) (flag-el (:flag @user)) (icon-el "caret down")]
          [:div {:class "ui item"}
           [:div {:class "ui labeled icon button"
                  :onClick #(rf/dispatch [:launch-db-modal])}
            (icon-el "search") "db"]]]]]])))

(defn main-view
  [active-page]
  [:div {:class "one column row"}
   [:div {:class "column"}
    (case @active-page :calendar [calendar-view]
      :planbook [planbook-view]
      :classes  [classes-view]
      [:span "No Page Found!"])]])

(defn container
  []
  (let [active-page (rf/subscribe [:active-page])
        modal?      (rf/subscribe [:modal :active?])]
    (fn []
      [:div {:class "ui grid container"
             :style #js {:margin 0}}
       [dimmer {:active? @modal?}]
       [global-modal]
       [top-bar active-page]
       [main-view active-page]])))
