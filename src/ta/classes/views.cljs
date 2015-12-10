(ns ta.classes.views
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]])
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [ta.common.util :refer [weekdays colors color-strings]]
            [ta.common.components :refer [sem e->val icon-el]]))

(defcard "#Classes")

(def new-class {:name "New Class"
                :editing? true
                :color (rand-nth colors)
                :schedule {:mon   [:slot :slot :slot :slot :slot]
                           :tues  [:slot :slot :slot :slot :slot]
                           :wed   [:slot :slot :slot :slot :slot]
                           :thurs [:slot :slot :slot :slot :slot]
                           :fri   [:slot :slot :slot :slot :slot]}})

(defcard new-class
  new-class)

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
         (icon-el "small circle")])])])

(defcard-rg schedule-table
  [schedule-table {:schedule (:schedule new-class)}])

; (defn week-schedule
;   []
;   (let [schedule (rf/subscribe [:schedule])
;         classes  (rf/subscribe [:classes])]
;    [:div {:class "ui fluid card"}
;     [:div {:class "content"}
;      [:div {:class "center aligned header"}
;       "Your Weekly Timetable"]]
;     [:div {:class "content"}
;      [schedule-table {:schedule @schedule :classes @classes}]]]))

(defn color-selector
  [{:keys [on-change selected-color]}]
  [:div {:class "ui mini horizontal list"}
    (for [color colors :let [color-str (get color-strings color)
                             selected? (= selected-color color)]]
     ^{:key color-str}
     [:div {:class "item"}
       [:a {:class (sem "ui" color-str "circular icon label")
            :on-click #(on-change color)}
         (if selected? "✓")]])])

(defcard-rg color-selector
  (fn [data _] (color-selector {:selected-color (:color @data)
                                :on-change #(swap! data assoc :color %)}))
  {:color :red}
  {:inspect-data true})

(defn schedule-selector
  "When a slot is clicked, on-change is called with an updated schedule map"
  [{:keys [on-change class-schedule selected-color id]}]
  (let [schedule nil ;FIXME@!!!!!!
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
                             :else      (icon-el "small circle"))]))])])))

(defcard-rg schedule-selector
  [schedule-selector {:on-change nil
                      :class-schedule nil
                      :selected-color :red
                      :id nil}])

(defn class-card
  [{:keys [id name color schedule editing? on-color-change on-schedule-change]}]
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
       {:on-change on-color-change
        :selected-color color}]
      [schedule-selector
       {:on-change on-schedule-change
        :class-schedule schedule
        :selected-color color
        :id id}]])])

(def schedule-atom (rg/atom {:color :green}))

(defcard-rg class-card
  [class-card {:id :abc
               :name "Year 11 ATAR English"
               :color (:color @schedule-atom)
               :schedule nil
               :editing? true
               :on-color-change #(swap! schedule-atom assoc :color %)
               :on-schedule-change #(print %)}]

  schedule-atom
  {:inspect-data true})

(def count-atom (rg/atom 0))

(defn counter
  [num]
  [:div
    [:button {:on-click #(swap! count-atom inc)}
      "Go!"]
    [:span num]])

(defn shell-component
  []
  [counter @count-atom])

(defcard-rg counter-test
  [shell-component])

; (defn classes-view
;   []
;   [:div {:class "ui grid"}
;    [:div {:class "two column row"}
;     [:div {:class "column"}
;      #_[class-list]
;      [:div {:class "ui center aligned basic segment"}
;       [:button {:class "ui labeled icon button"
;                 :on-click #(rf/dispatch [:class :new])}
;        (icon-el "plus") "Add Class"]]]
;     [:div {:class "column"}
;        [week-schedule]]]])
