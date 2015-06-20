(ns ta.views
    (:require [re-frame.core :as re-frame]
              [clojure.string :as string]))

(enable-console-print!)

(def weekdays [:mon :tues :wed])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"})

(defn align [direction el]
  [:div {:class (case direction :left "pull-left"
                                :right "pull-right"
                                :center "center")}
    el])

(defn icon ;Font Awesome icons - to use: edit close bars
  ([name] (icon name :s))
  ([name size]
    (let [size-str (case size
                         :s nil
                         :m "fa-lg"
                         :l "fa-2x")
          name-str (str "fa-" name)]
    [:i {:class (string/join " " ["fa" name-str size-str])}])))

(def flag
  [:i {:class "australia flag" :style #js {:padding-left 5}}])

(def page-links {:timetable {:icon (icon "calendar" :m)
                             :label "Timetable"
                             :url "#/timetable"}
                 :planner   {:icon (icon "edit" :m)
                             :label "Lesson Planner"
                             :url "#/planner"}})

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

(defn top-bar [active-page]
  (let [name (re-frame/subscribe [:username])]
    (fn []
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
                  (icon "gear" :m)]]]]])))

(defn panel [content]
  [:div {:class "ui raised segment"}
    [:span content]])

(defn lesson-panel[data]
  [panel (:title data) [:div (:text data)]])

;TODO: panels only use lesson title atm, none of the content
(defn class-slot [period lesson]
  (if (= :dot period)
    [panel (icon "coffee")]
    [panel [:div period (align :right (icon "cog"))]
      [lesson-panel lesson]]))

(defn weekday [day]
  (let [lessons   (re-frame/subscribe [:lessons day])
        timetable (re-frame/subscribe [:timetable day])]
    (fn []
      [:div
        [:p (align :center (day-strings day))]
        (map #(with-meta
          ;TODO: Fix this hacky rand-int crap
          (vector class-slot %1 %2) {:key (rand-int 1000)}) @timetable @lessons)])))

(defn week-view []
  [:div {:class "ui centered grid"}
    (map #(with-meta
      (vector :div {:class "five wide column"} [weekday %]) {:key %}) weekdays)])

(defn timetable-panel []
  (let []
    (fn []
      [week-view])))

(defn planner-panel []
  (let []
    (fn []
      [:span "Oh yeah it's a planner here"])))

(defn main-panel [active-page]
  (let []
    (fn []
      [:div {:class "row"}
        [:div {:class "column"}
          (case @active-page
                :timetable [timetable-panel]
                :planner [planner-panel]
                [:span "No Panel Found?"])]])))

(defn app []
  (let [active-page (re-frame/subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 10}}
        [top-bar active-page]
        [main-panel active-page]])))
