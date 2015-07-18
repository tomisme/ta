(ns ta.db
  (:require [matchbox.core :as m]
            [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]))

(def root (m/connect "https://frederick.firebaseio.com/"))

(def classes (m/get-in root [:classes]))

(defn add-new-class [class]
  (m/conj! classes class))

(defn setup-class-listener []
  (m/listen-to classes :value #(rf/dispatch [:update-classes (second %)])))

(defn setup-listeners []
  (setup-class-listener))

(def default-db
  { :active-page :timetable
    :timetable-view :week
    :active-week 11
    :new-class {:color :blue}

    :schedule {:mon   [:slot :slot :lunch :slot :slot :lunch :slot]
               :tues  [:slot :slot :lunch :slot :slot :lunch :slot]
               :wed   [:slot :slot :lunch :slot :slot :lunch :slot]
               :thurs [:slot :slot :lunch :slot :slot :lunch :slot]
               :fri   [:slot :slot :lunch :slot :slot :lunch :slot]}

    :user {:name "Tom Hutchinson"
           :flag :australia}

    :timetable {:mon   [:dot :dot :dot :dot :dot]
                :tues  [:dot :dot :dot :dot :dot]
                :wed   [:dot :dot :dot :dot :dot]
                :thurs [:dot :dot :dot :dot :dot]
                :fri   [:dot :dot :dot :dot :dot]}

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
                                          :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}]})
