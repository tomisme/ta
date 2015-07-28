(ns ta.views.planbook
  (:require [ta.views.common :refer [ibut sem e->val icon checkbox]]
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
                    [:a {:class "header"} title]]])

(defn lesson-list [lessons]
  (if (seq lessons)
    [:div {:class "ui divided items"}
      (for [lesson lessons]
        [lesson-item lesson])]
    [:span "Loading Lessons..."]))

(defn lesson-detail []
  [:div {:class "ui segment"}
    [:div {:class "ui form"}
      [:div {:class "fields"}
        [:div {:class "field"}
          [:select
            [:option {:value ""} "Year"]
            [:option {:value "7"} "7"]]]
        [:div {:class "field"}
          [:select
            [:option {:value ""} "Subject"]
            [:option {:value "English"} "English"]]]
        [:div {:class "field" :style #js {:marginTop 8 :float "right"}}
          [checkbox #() "Finished" true]]]
      [:input {:type "text" :placeholder "Enter a one line description for your lesson"}]]])

(defn planbook-panel []
  (let [lessons test-lessons #_(subscribe [:lessons])]
    [:div {:class "ui grid"}
      [:div {:class "four wide column"}
        [lesson-list lessons]]
      [:div {:class "twelve wide column"}
        [lesson-detail]]]))
