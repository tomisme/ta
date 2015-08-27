(ns ta.views.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [shodan.inspection :refer [inspect]]
            [ta.views.planbook.planbook :refer [planbook-view]]
            [ta.views.calendar :refer [calendar-view]]
            [ta.views.classes :refer [classes-view]]
            [ta.views.common :refer [sem icon flag-img]]
            [re-frame.core :refer [subscribe dispatch]]))

(defn sem-dimmer
  [{:keys [active?]}]
  [:div {:class (sem "ui dimmer #_modals page #_transition #_visible"
                     (if active? "active"))}])

(def test-modal-deets {:i "coffee"
                       :header "Coffee Time?"
                       :description "Would you like some coffee?"})

(defn sem-modal
  [{:keys [active? i header description on-yes on-no on-close scrollbox? text]}]
  [:div {:class (sem "ui modal transition #_active"
                     (if active? "visible"))
         :style {:top "20%"}}
    [:i {:class "close icon"
         :on-click #(dispatch [:modal :close])}]
    [:div {:class "header"} header]
    (if scrollbox? [:div {:style {:overflow-y "auto"
                                  :max-height "400px"}} text])
    [:div {:class "image content"}
      [:div {:class "image"} (icon i)]
      #_[:div {:class "description"}
        [:p description]]]
    [:div {:class "actions"}
      [:div {:class "two fluid ui buttons"}
        [:div {:class "ui red basic button"} (icon "remove")]
        [:div {:class "ui green basic button"} (icon "checkmark")]]]])

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
                (:name @user) (flag-img (:flag @user)) (icon "caret down")]
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
                         [:span "No Panel Found?"])]])

(defn app-container []
  (let [active-page (subscribe [:active-page])
        modal       (subscribe [:modal])]
    (fn []
      (let [active-modal? (get @modal :active?)]
        [:div {:class "ui grid container"
               :style #js {:margin 0}}
          [sem-dimmer {:active? (get @modal :active?)}]
          [sem-modal (assoc (get @modal :data) :active? active-modal?)]
          [top-bar active-page]
          [main-view active-page]]))))
