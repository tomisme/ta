(ns ^:figwheel-always ta.core
    (:require [reagent.core :as reagent :refer [atom]]))

(def state (atom {:username "Aaron Graham"
                  :view :week
                  :lessons {:mon  [{:class "11 General English"
                                    :text "MOAR MOVIES"}
                                   {:class "8 Media"
                                    :text "Well, obviosly movies"}]
                            :tues [{:class "8 Media"
                                    :text "Maybe we'll watch some movies"}]
                            :wed  [{:class "8 Media"
                                    :text "MOAAAAAR MOVIES"}
                                   {:class "11 General English"
                                    :text "Da MOVIES"}]}}))

(def day-strings {:mon "Monday"
                  :tues "Tuesday"
                  :wed "Wednesday"})

(def panel-styles {:default "panel-default"
                   :primary "panel-primary"
                   :success "panel-success"
                   :info "panel-info"
                   :warning "panel-warning"
                   :danger "panel-danger"
                   nil "panel-default"})

(defn nav-bar [brand name]
  [:nav {:class "navbar navbar-default"}
    [:div {:class "container"}
      [:div {:class "navbar-header"}
        [:a {:class "navbar-brand" :href "#"} brand]]
      [:p {:class "navbar-text navbar-right"} "Logged in as "
        [:a {:href "#"} name]]]])

(defn panel
  ([content]
    [:div {:class "panel panel-default"}
      [:div {:class "panel-body"} content]])
  ([title content]
    (panel title content :default))
  ([title content style]
    {:pre [(contains? panel-styles style)]}
    [:div {:class (str "panel " (panel-styles style))}
      [:div {:class "panel-heading"}
        [:h3 {:class "panel-title"} title]]
      [:div {:class "panel-body"} content]]))

(defn lesson [title content]
  [panel title [:div content] :primary])

(defn weekday [day]
  (let [lessons (get-in @state [:lessons day])]
    (fn []
      [:div
        [:p [:center (day-strings day)]]
        (for [l lessons]
          [lesson (:class l) (:text l)])])))

(defn week-view []
  [:div {:class "container"}
    [:div {:class "row"}
      [:div {:class "col-xs-4"}
        [weekday :mon]]
      [:div {:class "col-xs-4"}
        [weekday :tues]]
      [:div {:class "col-xs-4"}
        [weekday :wed]]]])

(defn app []
  [:div
    [nav-bar "Zen Teacher" (@state :username)]
    [week-view]])

(reagent/render-component [app] (. js/document (getElementById "app")))
