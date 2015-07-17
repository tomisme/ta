(ns ta.db
  (:require [matchbox.core :as m]
            [re-frame.core :as re-frame]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

(def root (m/connect "https://frederick.firebaseio.com/"))

(def classes (m/get-in root [:classes]))

(defn setup-fb-listener []
  #_(m/auth-anon) ; When do I need this?? May be time to read fb docs
  (m/listen-to classes :value #(re-frame/dispatch [:fb-update (second %)])))

(def default-db
  { :active-page :timetable
    :timetable-view :week
    :active-week 11

    :user {:name "Tom Hutchinson"
           :flag :australia}

    :timetable {:mon  [:dot "8 Media" "11 General English"]
                :tues ["11 General English" :dot :dot]
                :wed  ["8 Media" "10 Modified English" :dot]}

    :lessons {:mon  [{}
                     {:title "Film"
                      :text "Editing horror movie practise footage"}
                     {:title "Autobiographies"
                      :text "Well, an autobiography movie!"}]
              :tues [{:title "Autobiographies"
                      :text "Maybe we'll watch some movies"}
                      {}
                      {}]
              :wed  [{:title "Film"
                      :text "MOAAAAAR MOVIES"}
                     {:title "Visual Texts"
                      :text "Da MOVIES"}
                      {}]}})

(def test-lesson
  { :lessons [{:subject "English"
               :class "Year 8"
               :level "General"
               :activities [{:tags [{:text "japan"}
                                    {:text "poetry"}]
                             :description "Students write a Haiku"
                             :length 20 ;in minutes
                             :resources [{:sides 2
                                          :type :worksheet
                                          :format :pdf
                                          :description "Haiku Starter"
                                          :source "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}]})

(def resource-types [:worksheet])
