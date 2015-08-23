(ns ta.views.planbook.planbook
  (:require [ta.views.common :refer [sem icon]]
            [ta.views.planbook.activities :refer [activities-tab]]
            [ta.views.planbook.lessons :refer [lessons-tab]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(defn planbook-view
  []
  (let [active-tab (subscribe [:open :tab])
        tabs [{:key :activities :str "Activities" :i "cubes"}
              {:key :lessons    :str "Lessons"    :i "file outline"}
              {:key :units      :str "Units"      :i "briefcase"}]]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "centered one colum row"}
          [:div {:class "colum"}
            (doall
              (for [tab tabs :let [{:keys [key str i]} tab]]
                ^{:key str}
                  [:button {:on-click #(dispatch [:set-open :tab key])
                            :class (sem (if (= @active-tab key) "active")
                                        "ui labeled icon button")}
                    (icon i) str]))]]
       (case @active-tab
          :units      [:p "Lets make some units!"]
          :lessons    [lessons-tab]
          :activities [activities-tab])])))