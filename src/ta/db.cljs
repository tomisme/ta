(ns ta.db
  (:require [ta.util :refer [colors]]
            [matchbox.core :as m]
            [re-frame.core :refer [dispatch]]
            [shodan.inspection :refer [inspect]]))

(def root (m/connect "https://frederick.firebaseio.com/"))

(def classes (m/get-in root [:classes]))

(defn add-new-class! [class]
  (m/conj! classes class))

(defn setup-class-listener []
  (m/listen-to classes :value (fn [[_ v]]
                                (dispatch [:update-classes v]))))

(defn setup-listeners []
  (setup-class-listener))

(defn default-new-class-data []
  {:color (rand-nth colors)
   :schedule {:mon   [:slot :slot :slot :slot :slot]
              :tues  [:slot :slot :slot :slot :slot]
              :wed   [:slot :slot :slot :slot :slot]
              :thurs [:slot :slot :slot :slot :slot]
              :fri   [:slot :slot :slot :slot :slot]}})

(def default-db {:active-page :timetable
                 :timetable-view :week
                 :active-week 11
                 :new-class (default-new-class-data)
                 :user {:name "Tom Hutchinson"
                        :flag :australia}})

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
