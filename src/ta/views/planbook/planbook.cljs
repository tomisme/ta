(ns ta.views.planbook.planbook
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.views.common :refer [sem icon-el menu-el]]
            [ta.views.planbook.activities :refer [activities-tab]]
            [ta.views.planbook.lessons :refer [lessons-tab]]))

(def planbook-tabs [{:key :activities :label "Activities" :icon "cubes"}
                    {:key :lessons    :label "Lessons"    :icon "browser"}
                    {:key :units      :label "Units"      :icon "briefcase"}])

(defn planbook-view
  []
  (let [active-tab (rf/subscribe [:open :tab])]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "centered one colum row"}
          [menu-el {:items planbook-tabs
                    :active-items @active-tab
                    :on-change #(rf/dispatch [:set-planbook-open :tab %])}]]
       (case @active-tab
         :units      [:p "Lets make some units!"]
         :lessons    [lessons-tab]
         :activities [activities-tab])])))

(defcard planbook-menu-test
  (dc/reagent [menu-el {:items planbook-tabs
                        :active-item :activities
                        :on-change #(inspect %)}]))
