(ns ta.views.planbook
  (:require [ta.views.common :refer [ibut sem e->val icon]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def test-lessons [{:title "Haikus 1"
                    :subject "English"
                    :year 8
                    :activities [{:tags [{:text "japan"}
                                         {:text "poetry"}]
                                  :description "Students write a Haiku"
                                  :length 20 ;in minutes
                                  :resources [{:sides 2
                                               :type :worksheet
                                               :format :pdf
                                               :description "Haiku Starter"
                                               :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}
                   {:title "Haikus 2"
                    :subject "English"
                    :year 8
                    :activities [{:tags [{:text "japan"}
                                         {:text "poetry"}]
                                  :description "Students write a Haiku"
                                  :length 20 ;in minutes
                                  :resources [{:sides 2
                                               :type :worksheet
                                               :format :pdf
                                               :description "Haiku Starter"
                                               :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}])

(defn lesson-item [{:keys [title subject year activities]}]
  ^{:key title} [:div {:class "item"}
                  [:div {:class "content"}
                    [:span {:class "header"} title]]])

(defn lesson-list []
  (let [lessons test-lessons #_(subscribe [:lessons])]
    (fn []
      (if (seq lessons)
        [:div {:class "ui divided items"}
          (for [lesson lessons]
            [lesson-item lesson])]
        [:span "Loading Lessons..."]))))

(defn lesson-detail []
  [:span "Select a lesson to work on"])

(defn planbook-panel []
  (let []
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "four wide column"}
          [lesson-list]]
        [:div {:class "six wide column"}
          [lesson-detail]]])))
