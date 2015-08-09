(ns ta.views.classes
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.util :refer [weekdays colors color-strings]]
            [ta.views.common :refer [sem e->val icon]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

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
    [:div {:class "ui fluid card"}
      [:div {:class "content"}
        [:div {:class "center aligned header"}
          "Your Weekly Timetable"]]
      [:div {:class "content"}
        [schedule-table @schedule @classes]]]))

(defn color-selector
  [{:keys [on-change selected-color]}]
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
   with an updated schedule map"
  [{:keys [on-change class-schedule selected-color id]}]
  (let [schedule @(subscribe [:schedule])
        day-labels {:mon   "Mo"
                    :tues  "Tu"
                    :wed   "We"
                    :thurs "Th"
                    :fri   "Fr"}]
    (fn [{:keys [on-change class-schedule selected-color]}]
      [:div {:class "ui equal width center aligned padded grid"}
        (for [row (conj (range 5) :label)] ;; prepend :label
         ^{:key row} [:div {:class "row"}
           (for [col (into [:label] weekdays)] ;; prepend :label
             ;; what kind if table cell are we?
             (let [col-label? (= col :label)
                   row-label? (= row :label)
                   cell-id    (get-in schedule [col row])
                   taken?     (and (not= cell-id id) cell-id)
                   slot?      (not (or col-label? row-label? taken?))
                   selected?  (if slot?
                                (= (get-in class-schedule [col row]) :selected))
                   on-select  (if slot?
                                #(on-change (assoc-in class-schedule
                                                      [col row] (if selected?
                                                                  :slot
                                                                  :selected))))
                   color-str  (cond taken? "black"
                                    selected? (selected-color color-strings))]
               ^{:key col}
                 [:div {:class (sem color-str "column")
                        :on-click on-select}
               (cond
                 row-label? [:span (col day-labels)]
                 col-label? [:span (str "S" (inc row))]
                 taken?     (icon "close")
                 selected?  (icon "check")
                 :else      (icon "circle" :s))]))])])))

(defn class-card
  [[id {:keys [name color schedule editing?] :as class}]]
  ^{:key id}
    [:div {:class (sem "ui fluid card" (get color-strings color))}
      [:div {:class "content"}
        [:div {:class "header"}
          [:div {:class (sem "ui large" (get color-strings color) "label")} name]
          (if editing?
            [:div {:class "right floated"}
              [:button {:class "ui red icon button"
                        :onClick #(dispatch [:class :delete id])}
                (icon "trash")]
              [:button {:class "ui green icon button"
                        :onClick #(dispatch [:class :update id :editing? false])}
                (icon "check")]]
            [:div {:class "right floated"}
              [:button {:class "ui green icon button"
                        :onClick #(dispatch [:class :update id :editing? true])}
                (icon "edit")]])]]
      (if editing?
        [:div {:class "center aligned content"}
          [color-selector
            {:on-change #(dispatch [:class :update id :color %])
             :selected-color color}]
          [schedule-selector
            {:on-change #(dispatch [:class :update id :schedule %])
             :class-schedule schedule
             :selected-color color
             :id id}]])])

(defn classes-view
  []
  (let [classes (subscribe [:classes])]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "two column row"}
          [:div {:class "column"}
            (if (seq @classes)
              [:div (map class-card @classes)]
              [:span "Loading Classes..."])
            [:div {:class "ui center aligned basic segment"}
              [:button {:class "ui labeled icon button"
                        :on-click #(dispatch [:class :new])}
                (icon "plus") "Add Class"]]]
          [:div {:class "column"}
            [week-schedule]]]])))
