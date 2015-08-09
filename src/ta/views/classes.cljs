(ns ta.views.classes
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.util :refer [weekdays colors color-strings]]
            [ta.views.common :refer [sem e->val icon]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(defn class-schedule
  [schedule color]
  [:div {:class "ui equal width center aligned padded grid"}
    (for [session (range 5)]
      ^{:key session}
        [:div {:class "row"}
          (for [day weekdays
                :let [cell (get-in schedule [day session])]]
            ^{:key (str day session)}
              [:div {:class (str (if (= :selected cell) (color color-strings))
                                 " column")}
               (icon "circle" :s)])])])

(defn schedule-table
  [schedule classes]
  [:div {:class "ui equal width center aligned padded grid"}
    (for [session (range 5)]
      ^{:key session}
        [:div {:class "row"}
          (for [day weekdays
                :let [class     (get-in schedule [day session])
                      color-str (if class (get color-strings
                                               (:color (get classes class))))]]
            ^{:key (str day session)}
              [:div {:class (str color-str " column")}
                (icon "circle" :s)])])])

(defn week-schedule
  []
  (let [schedule (subscribe [:schedule])
        classes  (subscribe [:classes])]
    [:div {:class "ui card"}
      [:div {:class "content"}
        [:div {:class "center aligned header"}
          "Your Weekly Timetable"]]
      [:div {:class "content"}
        [schedule-table @schedule @classes]]]))

(defn class-card
  [[id {:keys [name color schedule] :as class}]]
  ^{:key id}
    [:div {:class (sem "ui card" (get color-strings color))}
      [:div {:class "content"}
        name
        [:div {:class "right floated"}
          [:button {:class "ui red icon button"
                    :onClick #(dispatch [:class :delete id])}
            (icon "trash")]]]
      [:div {:class "content"} [class-schedule schedule color]]])

(defn color-selector
  [on-change selected-color]
  [:div {:class "ui mini horizontal list"}
    (doall (for [color colors
                 :let [color-str (get color-strings color)
                       selected? (= selected-color color)]]
             ^{:key color-str}
               [:div {:class "item"}
                 [:a {:class (sem "ui" color-str "circular icon label")
                      :on-click #(on-change color)}
                   (if selected? "✓")]]))])

(defn schedule-selector
  "When an empty class slot in the table is clicked, it calls on-change
   with an updated weekly timetable"
  [on-change schedule new-schedule selected-color]
  (let [day-labels {:mon   "Mo"
                    :tues  "Tu"
                    :wed   "We"
                    :thurs "Th"
                    :fri   "Fr"}]
    [:div {:class "ui equal width center aligned padded grid"}
      (for [row (conj (range 5) :label)] ;; prepend :label
       ^{:key row} [:div {:class "row"}
         (for [col (into [:label] weekdays)] ;; prepend :label
           ;; what kind if table cell are we?
           (let [col-label? (= col :label)
                 row-label? (= row :label)
                 class?     (get-in schedule [col row])
                 slot?      (not (or col-label? row-label? class?))
                 selected?  (if slot?
                              (= (get-in new-schedule [col row]) :selected))
                 on-select  (if slot?
                              #(on-change (assoc-in new-schedule
                                                    [col row] (if selected?
                                                                :slot
                                                                :selected))))
                 color-str  (cond class? "black"
                                  selected? (selected-color color-strings))]
             ^{:key col}
               [:div {:class (sem color-str "column")
                      :on-click on-select}
             (cond
               row-label? [:span (col day-labels)]
               col-label? [:span (str "S" (inc row))]
               class?     (icon "close")
               selected?  (icon "check")
               :else      (icon "circle" :s))]))])]))

(defn new-class-form
  []
  (let [schedule           (subscribe [:schedule])
        new-class          (subscribe [:new-class])
        new-class-name     (reaction (:name     @new-class))
        new-class-color    (reaction (:color    @new-class))
        new-class-schedule (reaction (:schedule @new-class))]
    (fn []
      [:div {:class "ui card"}
        [:div {:class "center aligned content"}
          [:div {:class "header"} "Create a new class"]
          [:div {:class "meta"} "It will be added to your timetable"]]
        [:div {:class "center aligned content"}
          [:div {:class "ui fluid input"}
            [:input {:type "text"
                     :value @new-class-name
                     :placeholder "Choose a name for the class"
                     :on-change #(dispatch [:update-new-class
                                            :name (e->val %)])}]]
          [schedule-selector #(dispatch [:update-new-class :schedule %])
                             @schedule
                             @new-class-schedule
                             @new-class-color]
          [color-selector #(dispatch [:update-new-class :color %])
                          @new-class-color]]
        [:div {:class "center aligned extra content"}
          [:button {:class "ui labeled icon button"
                    :on-click #(dispatch [:add-new-class])}
            (icon "plus")
            "Add Class"]]])))

(defn classes-view
  []
  (let [classes (subscribe [:classes])]
    (fn []
      [:div {:class "ui centered grid"}
        [:div {:class "row"}
          [:div {:class "eight wide column"}
            (if (seq @classes)
              [:div (map class-card @classes)]
              [:span "Loading Classes..."])]
          [:div {:class "eight wide column"}
            [week-schedule]
            [new-class-form]]]])))
