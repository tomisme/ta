(ns ta.views
    (:require [re-frame.core :as re-frame]
              [shodan.console :as console :include-macros true]
              [shodan.inspection :refer [inspect]]
              [clojure.string :as string]))

(def weekdays [:mon :tues :wed])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"})

(defn sem [& bits]
  "Return a string for use as an HTML component's :class"
  (string/join " " bits))

(defn icon
  "Return a Font Awesome icon from string [name]"
  ([name] (icon name :s))
  ([name size]
    (let [size-str (case size
                         :s nil
                         :m "fa-lg"
                         :l "fa-2x")
          name-str (str "fa-" name)]
    [:i {:class (sem "fa" name-str size-str)}])))

(defn flag-img [flag]
  (case flag
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]))

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
                 icon [:span {:style #js {:paddingRight 8}} (:icon (second page))]
                 label (:label (second page))
                 url (:url (second page))]
             (with-meta
               (vector :a {:class class :href url} icon label) {:key label})))
         page-links)))

(defn top-bar [active-page]
  (let [user (re-frame/subscribe [:user])
        name (get-in @user [:name])
        flag (get-in @user [:flag])]
    (fn []
        [:div {:class "row"}
          [:div {:class "column"}
            [:div {:class "ui secondary pointing menu"}
              (nav-links active-page)
              [:div {:class "right menu"}
                [:a {:class "ui item"}
                  [:span {:style #js {:fontWeight "bold"}} name]
                  [flag-img flag]
                  [:span {:style #js {:paddingLeft 5}} (icon "caret-down")]]]]]])))

(defn class-slot [period lesson]
  (if (= :dot period)
    [:div {:class "ui raised center aligned blue segment"} (icon "coffee")]
    [:div {:class "ui raised green segment"}
      [:div {:class "ui label ribbon green"} period]
                             [:div {:class "pull-right"} (icon "pencil")]
      [:div {:class "ui top attached header segment"
             :style #js {:marginTop 10
                         :textDecoration "underline"}} (:title lesson)]
      [:div {:class "ui attached segment"} (:text lesson)]]))

(defn weekday [day]
  (let [lessons   (re-frame/subscribe [:lessons day])
        timetable (re-frame/subscribe [:timetable day])]
    (fn []
      [:div
        [:center (day-strings day)]
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
      [:p "Don't worry, you'll be able to plan lessons pretty soon. I can feel it."])))

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
