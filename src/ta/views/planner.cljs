(ns ta.views.planner
  (:require [ta.util :refer [colors color-strings]]
            [ta.views.common :refer [sem e->val icon]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(defn planner-panel []
  (let []
    (fn []
      [:p "Don't worry, you'll be able to plan lessons pretty soon. I can feel it."])))
