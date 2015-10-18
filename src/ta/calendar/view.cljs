(ns ta.calendar.view
  (:require-macros [secretary.core :refer [defroute]]
                   [devcards.core :as dc :refer [defcard defcard-doc deftest]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.util :refer [weekdays day-strings color-strings]]
            [ta.common.remantic :refer [sem icon-el]]))

(defn class-slot [id classes]
  (if (not id)
    [:div {:class "ui card"}
     [:div {:class "center aligned content"} (icon-el "coffee")]]
    (let [{:keys [name color schedule] :as class} (id @classes)
          color-str (color color-strings)]
      [:div {:class (sem "ui" color-str "card")}
       [:div {:class "content"}
        [:div {:class (sem "ui" color-str "label ribbon")
               :style #js {:marginBottom 10}} name]
        [:div {:class "description"} (str "Click to add a lessson")]]])))

(defn weekday [day]
  (let [schedule (rf/subscribe [:schedule])
        classes  (rf/subscribe [:classes])]
    (fn []
      [:div
       [:center (day-strings day)]
       (for [slot (day @schedule)]
         ^{:key (rand-int 1000)} ;; TODO: work out a better key
         [class-slot slot classes])])))

(defn week-view []
  (let [week (rf/subscribe [:active-week])]
    [:div {:class "ui centered grid"}
     [:div {:class "row"}
      [:div {:class "center aligned column"}
       [:a {:href (str "#/calendar/week/" (dec @week))}
        (icon-el "chevron circle left")]
       (str "Week " @week)
       [:a {:href (str "#/calendar/week/" (inc @week))}
        (icon-el "chevron circle right")]]]
     [:div {:class "row"}
      (for [day weekdays]
        ^{:key day} [:div {:class "five wide column"}
                     [weekday day]])]]))

(defn calendar-view []
  (let []
    (fn []
      [week-view])))
