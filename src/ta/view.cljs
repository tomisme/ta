(ns ta.view
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [ta.common.remantic :refer [sem icon-el flag-el]]
            [ta.common.modal :refer [global-modal dimmer]]
            [ta.planbook.view :refer [planbook-view]]
            [ta.calendar.view :refer [calendar-view]]
            [ta.classes.view :refer [classes-view]]))

(defn top-bar
  [{:keys [user active-page]}]
  (let [pages {:calendar {:i "calendar"
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
                         :let [class (sem (if (= active-page k) "active") "item")
                               icon [:span {:style #js {:paddingright 4}} (icon-el i)]]]
                     ^{:key label} [:a {:class class :href url} icon label]))
            [:div {:class "right menu"}
              [:a {:class "ui item"}
                (:name user) (flag-el (:flag user)) (icon-el "caret down")]
              [:div {:class "ui item"}]]]]])))

(defn main-view
  [{:keys [active-page]}]
  [:div {:class "one column row"}
    [:div {:class "column"}
      (case active-page :calendar [calendar-view]
        :planbook [planbook-view]
        :classes  [classes-view]
        [:span "No Page Found!"])]])

(defn container
  []
  (let [page   (rf/subscribe [:active-page])
        modal? (rf/subscribe [:modal :active?])
        user   (rf/subscribe [:user])]
    (fn []
      [:div {:class "ui grid container"
             :style #js {:margin 0}}
        [global-modal]
        [dimmer    {:active? @modal?}]
        [top-bar   {:active-page @page :user @user}]
        [main-view {:active-page @page}]])))
