(ns ta.views.planbook.planbook
  (:require [ta.views.common :refer [sem icon-el]]
            [ta.views.planbook.activities :refer [activities-tab]]
            [ta.views.planbook.lessons :refer [lessons-tab]]
            [re-frame.core :as r]
            [shodan.inspection :refer [inspect]]))

(defn planbook-view
  []
  (let [active-tab (r/subscribe [:open :tab])
        tabs [{:key :activities :label "Activities" :icon "cubes"}
              {:key :lessons    :label "Lessons"    :icon "file outline"}
              {:key :units      :label "Units"      :icon "briefcase"}]]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "centered one colum row"}
          [:div {:class "ui menu"}
            (doall (for [tab tabs :let [{:keys [key label icon]} tab]]
                     ^{:key label}
                       [:a {:class (sem "item" (if (= @active-tab key) "active"))
                            :on-click #(r/dispatch [:set-planbook-open :tab key])}
                         [:span {:style {:marginRight 5}} (icon-el icon)] label]))]]
       (case @active-tab
          :units      [:p "Lets make some units!"]
          :lessons    [lessons-tab]
          :activities [activities-tab])])))
