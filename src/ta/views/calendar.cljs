(ns ta.views.calendar
  (:require [ta.util :refer [weekdays day-strings color-strings]]
            [ta.views.common :refer [sem icon]]
            [re-frame.core :refer [subscribe]]
            [shodan.inspection :refer [inspect]]))

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
          [:a {:href (str "#/calendar/week/" (dec @week))}
            (icon "chevron circle left")]
          (str "Week " @week)
          [:a {:href (str "#/calendar/week/" (inc @week))}
            (icon "chevron circle right")]]]
      [:div {:class "row"}
        (for [day weekdays]
          ^{:key day} [:div {:class "five wide column"}
                        [weekday day]])]]))

(defn calendar-view []
  (let []
    (fn []
      [week-view])))
