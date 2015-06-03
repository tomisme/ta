(ns ^:figwheel-always ta.core
    (:require [reagent.core :as reagent :refer [atom]]
              [ta.re-boot :refer [align panel icon]]))

(def state (atom {:username "Aaron Graham"
                  :view :week
                  :timetable {:mon  [:dot "8 Media" "11 General English"]
                              :tues ["11 General English" :dot :dot]
                              :wed  ["8 Media" "10 Modified English" :dot]}
                  :lessons {:mon  [{}
                                   {:title "Film #2"
                                    :text "MOAR MOVIES"}
                                   {:title "Autobiographies #4"
                                    :text "Well, an autobiography movie!"}]
                            :tues [{:title "Autobiographies #5"
                                    :text "Maybe we'll watch some movies"}
                                    {}
                                    {}]
                            :wed  [{:title "Film #3"
                                    :text "MOAAAAAR MOVIES"}
                                   {:title "Visual Texts #1"
                                    :text "Da MOVIES"}
                                    {}]}}))

(def weekdays [:mon :tues :wed])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"})

(defn nav-bar [brand name]
  [:nav {:class "navbar navbar-default"}
    [:div {:class "container"}
      [:div {:class "navbar-header"}
        [:a {:class "navbar-brand" :href "#"} brand]]
      [:p {:class "navbar-text navbar-right"}
        (icon "cogs")]
      [:p {:class "navbar-text navbar-right"}
        (icon "calendar")]
      [:p {:class "navbar-text navbar-right"}
        (icon "archive")]
      [:p {:class "navbar-text navbar-right"}
        "Logged in as "
        [:a {:href "#"} name]]]])

(defn lesson-panel[data]
  [panel (:title data) [:div (:text data)] :primary])

(defn class-slot [period lesson]
  (if (= :dot period)
    [panel (align :center (icon "coffee"))]
    [panel [:div period (align :right (icon "cog"))]
      [lesson-panel lesson]]))

(defn weekday [day]
  ;TODO: use destructuring to make this cleaner
  (let [lessons   (get-in @state [:lessons day])
        timetable (get-in @state [:timetable day])]
    (fn []
      [:div
        [:p (align :center (day-strings day))]
        (map #(vector class-slot %1 %2) timetable lessons)])))

(defn week-view []
  [:div {:class "container"}
    [:div {:class "row"}
      (map #(vector :div {:class "col-xs-4"} [weekday %]) weekdays)]])

(defn app []
  [:div
    [nav-bar "Zen Teacher" (@state :username)]
    [week-view]])

(reagent/render-component [app] (. js/document (getElementById "app")))
