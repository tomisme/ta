(ns ta.views.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.classes :refer [classes-panel]]
            [ta.views.common :refer [sem icon]]
            [ta.util :refer [weekdays day-strings colors color-strings]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(defn flag-img [country]
  "Takes a country keyword and returns a flag icon element"
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]))

(defn test-dropdown []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])

(def page-links [{:key :timetable
                  :icon "calendar"
                  :label "Timetable"
                  :url "#/timetable"}
                 {:key :planner
                  :icon "book"
                  :label "Planbook"
                  :url "#/planner"}
                 {:key :classes
                  :icon "table"
                  :label "Classes"
                  :url "#/classes"}])

(defn nav-links [current-page]
  (let [active-page @current-page]
    (map (fn [link]
           (let [class (sem (if (= (:key link) active-page) "active") "item")
                 icon [:span {:style #js {:paddingRight 4}} (icon (:icon link))]
                 label (:label link)
                 url (:url link)]
             ^{:key label} [:a {:class class :href url} icon label]))
         page-links)))

(defn top-bar [active-page]
  (let [user (subscribe [:user])
        name (reaction (:name @user))
        flag (reaction (:flag @user))]
    (fn []
        [:div {:class "row"}
          [:div {:class "column"}
            [:div {:class "ui secondary pointing menu"}
              (nav-links active-page)
              [:div {:class "right menu"}
                [:a {:class "ui item"}
                  @name (flag-img @flag) (icon "caret down")]]]]])))

(defn class-slot [id classes]
  (if (not id)
    [:div {:class "ui card"}
      [:div {:class "center aligned content"} (icon "coffee")]]
    (let [{:keys [name color schedule] :as class} (id @classes)
          color-str (color color-strings)]
      [:div {:class (sem "ui" color-str "card")}
        [:div {:class "content"}
          [:div {:class (sem "ui" color-str "label ribbon")
                 :style #js {:marginBottom 10}} name]
          [:div {:class "description"} (str "Click to add a lessson")]]])))

(defn weekday [day]
  (let [schedule (subscribe [:schedule])
        classes  (subscribe [:classes])]
    (fn []
      [:div
        [:center (day-strings day)]
        (for [slot (day @schedule)]
          ^{:key (rand-int 1000)} ;; TODO: work out a better key
            [class-slot slot classes])])))

(defn week-view []
  (let [week (subscribe [:active-week])]
    [:div {:class "ui centered grid"}
      [:div {:class "row"}
        [:div {:class "center aligned column"}
          [:a {:href (str "#/timetable/week/" (dec @week))}
            (icon "chevron circle left")]
          (str "Week " @week)
          [:a {:href (str "#/timetable/week/" (inc @week))}
            (icon "chevron circle right")]]]
      [:div {:class "row"}
        (for [day weekdays]
          ^{:key day} [:div {:class "five wide column"}
                        [weekday day]])]]))

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
          (case @active-page :timetable [timetable-panel]
                             :planner [planner-panel]
                             :classes [classes-panel]
                             [:span "No Panel Found?"])]])))

(defn container []
  (let [active-page (subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 10}}
        [top-bar active-page]
        [main-panel active-page]])))
