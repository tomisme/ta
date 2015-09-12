(ns ta.views.classes
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.util :refer [weekdays colors color-strings]]
            [ta.views.common :refer [sem e->val icon-el]]))

(defn schedule-table
  [{:keys [schedule classes]}]
  [:div {:class "ui equal width center aligned padded grid"}
    (for [session (range 5)]
      ^{:key session}
        [:div {:class "row"}
          (for [day weekdays
                :let [class-id  (get-in schedule [day session])
                      class     (get classes class-id)
                      color-str (get color-strings (:color class))]]
            ^{:key (str day session)}
              [:div {:class (str color-str " column")}
                (icon-el "circle" :s)])])])

(defn week-schedule
  []
  (let [schedule (rf/subscribe [:schedule])
        classes  (rf/subscribe [:classes])]
    [:div {:class "ui fluid card"}
      [:div {:class "content"}
        [:div {:class "center aligned header"}
          "Your Weekly Timetable"]]
      [:div {:class "content"}
        [schedule-table {:schedule @schedule :classes @classes}]]]))

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
  "When a slot is clicked, on-change is called with an updated schedule map"
  [{:keys [on-change class-schedule selected-color id]}]
  (let [schedule @(rf/subscribe [:schedule]) ;; eww. but lets you make changes
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
                 taken?     (icon-el "close")
                 selected?  (icon-el "check")
                 :else      (icon-el "circle" :s))]))])])))

(defn class-card
  [[id {:keys [name color schedule editing?] :as class}]]
  ^{:key id}
    [:div {:class (sem (if-not editing? "link")
                       (get color-strings color) "ui fluid card")
         :on-click (if-not editing?
                     #(rf/dispatch [:class :update id :editing? true]))}
      [:div {:class "content"}
        [:div {:class "header"}
          [:div {:class (sem (get color-strings color) "ui large label")}
            name]
          (if editing?
            [:div {:class "right floated"}
              [:button {:class "ui red icon button"
                        :onClick #(rf/dispatch [:class :delete id])}
                (icon-el "trash")]
              [:button {:class "ui green icon button"
                        :onClick #(rf/dispatch [:class :update id :editing? false])}
                (icon-el "check")]])]]
      (if editing?
        [:div {:class "center aligned content"}
          [color-selector
            {:on-change #(rf/dispatch [:class :update id :color %])
             :selected-color color}]
          [schedule-selector
            {:on-change #(rf/dispatch [:class :update id :schedule %])
             :class-schedule schedule
             :selected-color color
             :id id}]])])

(defn class-list
  []
  (let [classes (rf/subscribe [:classes])]
    (fn []
      (if (seq @classes)
        [:div (map class-card @classes)]
        [:span "Loading Classes..."]))))

(defn classes-view
  []
  [:div {:class "ui grid"}
    [:div {:class "two column row"}
      [:div {:class "column"}
        [class-list]
        [:div {:class "ui center aligned basic segment"}
          [:button {:class "ui labeled icon button"
                    :on-click #(rf/dispatch [:class :new])}
            (icon-el "plus") "Add Class"]]]
      [:div {:class "column"}
        [week-schedule]]]])
