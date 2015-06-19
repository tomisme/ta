(ns ta.views
    (:require [re-frame.core :as re-frame]
              [ta.bootstrap :refer [align panel icon]]))

(def weekdays [:mon :tues :wed])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"})

(defn top-bar [brand name]
  [:div {:class "testing container"
         :style #js {:width "700"
                     :padding-top "20"}}
    [:div {:class "ui segment"}
      [:a {:class "ui ribbon label"} brand]
      "Logged in as " [:i {:class "australian flag"}] name]])

#_(defn nav-bar [brand name]
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

#_(defn lesson-panel[data]
  [panel (:title data) [:div (:text data)] :primary])

#_(defn class-slot [period lesson]
  (if (= :dot period)
    [panel (align :center (icon "coffee"))]
    [panel [:div period (align :right (icon "cog"))]
      [lesson-panel lesson]]))

#_(defn weekday [day]
  ;TODO: use destructuring to make this cleaner
  (let [lessons   (get-in @state [:lessons day])
        timetable (get-in @state [:timetable day])]
    (fn []
      [:div
        [:p (align :center (day-strings day))]
        (map #(with-meta
          ;TODO: Fix this hacky rand-int crap
          (vector class-slot %1 %2) {:key (rand-int 1000)}) timetable lessons)])))

#_(defn week-view []
  [:div {:class "container"}
    [:div {:class "row"}
      (map #(with-meta
        (vector :div {:class "col-xs-4"} [weekday %]) {:key %}) weekdays)]])

(defn app []
  (let [username (re-frame/subscribe [:username])]
    (fn []
      [:div
        [top-bar "Zen Teacher" @username]
        #_[week-view]])))
