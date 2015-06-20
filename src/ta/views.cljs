(ns ta.views
    (:require [re-frame.core :as re-frame]
              [clojure.string :as string]))

(enable-console-print!)

(defn icon ;Font Awesome icons - to use: edit close bars
  ([name] (icon name :s))
  ([name size]
    (let [size-str (case size
                         :s nil
                         :m "fa-lg"
                         :l "fa-2x")
          name-str (str "fa-" name)]
    [:i {:class (string/join " " ["fa" name-str size-str])}])))

(def page-links {:timetable {:icon (icon "calendar" :m)
                             :label "Timetable"
                             :url "#/timetable"}
                 :planner   {:icon (icon "edit" :m)
                             :label "Lesson Planner"
                             :url "#/planner"}})

(def weekdays [:mon :tues :wed])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"})

(def flag
  [:i {:class "australia flag" :style #js {:padding-left 5}}])

(defn nav-links [current-page]
  (let [active-page @current-page]
    (map (fn [page]
           (let [class (str (if (= active-page (key page)) "active ") "item")
                 icon [:span {:style #js {:padding-right 8}} (:icon (second page))]
                 label (:label (second page))
                 url (:url (second page))]
             (with-meta
               (vector :a {:class class :href url} icon label) {:key label})))
         page-links)))

(defn top-bar [brand]
  (let [name (re-frame/subscribe [:username])
        active-page (re-frame/subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 10}}
        [:div {:class "row"}
          [:div {:class "column"}
            [:div {:class "ui secondary pointing menu"}
              (nav-links active-page)
              [:div {:class "right menu"}
                [:a {:class "ui item"}
                  [:span {:style #js {:padding-right 5}} (icon "caret-down")]
                  [:span {:style #js {:font-weight "bold"}} @name]
                  flag]
                [:a {:class "ui item"}
                  (icon "gear" :m)]]]]]])))

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
  (let []
    (fn []
      [:div
        [top-bar "Zen Teacher"]
        #_[week-view]])))
