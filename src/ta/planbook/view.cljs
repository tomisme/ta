(ns ta.planbook.view
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.common.remantic :refer [sem icon-el menu-el]]
            [ta.planbook.activities.view :as activities-view]
            [ta.planbook.lessons.view :as lessons-view]
            [ta.planbook.resources.view :as resources-view]))

(def tabs [{:key :resources  :label "Resources"  :icon "file"}
           {:key :activities :label "Activities" :icon "cubes"}
           {:key :lessons    :label "Lessons"    :icon "browser"}
           {:key :units      :label "Units"      :icon "briefcase"}])

(defn planbook-view
  []
  (let [active-tab (rf/subscribe [:open :tab])]
    (fn []
      [:div {:class "ui grid"}
       [:div {:class "centered two column row"}
        [:div {:class "column"}
         [menu-el {:items tabs
                   :active-item @active-tab
                   :on-change #(rf/dispatch [:set-planbook-open :tab %])}]]
        [:div {:class "column"}
         [:button {:class "ui labeled icon button"
                   :on-click #(rf/dispatch [:activity :new])}
          (icon-el "plus") "New Activity"]]]
       (case @active-tab
         :units      [:p "Lets make some units!"]
         :lessons    [lessons-view/lessons-tab]
         :activities [activities-view/activities-tab]
         :resources  [resources-view/resources-tab])])))

(defcard planbook-menu-test
  (dc/reagent [menu-el {:items tabs
                        :active-item :activities
                        :on-change #(inspect %)}]))
