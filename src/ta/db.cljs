(ns ta.db
  (:require [ta.util :refer [colors]]
            [matchbox.core :as m]
            [re-frame.core :refer [dispatch]]
            [shodan.inspection :refer [inspect]]))

(def root (m/connect "https://frederick.firebaseio.com/"))

(def classes (m/get-in root [:classes]))
(def lessons (m/get-in root [:lessons]))

(defn add-new-class! [class]
  (m/conj! classes class))

(defn add-lesson! [lesson]
  (m/conj! lessons lesson))

(defn update-lesson-attribute! [id attribute value]
  (m/reset-in! lessons [id attribute] value))

(defn setup-listeners []
  (m/listen-to classes
               :value (fn [[_ val]] (dispatch [:update-classes val])))
  (m/listen-to lessons
               :value (fn [[_ val]] (dispatch [:update-lessons val]))))

(defn default-new-class-data []
  {:color (rand-nth colors)
   :schedule {:mon   [:slot :slot :slot :slot :slot]
              :tues  [:slot :slot :slot :slot :slot]
              :wed   [:slot :slot :slot :slot :slot]
              :thurs [:slot :slot :slot :slot :slot]
              :fri   [:slot :slot :slot :slot :slot]}})

(def test-lessons {:a {:description "Haikus 1"
                    :subject "English"
                    :year 7
                    :finished true
                    :activities [{:tags [{:text "japan"}
                                         {:text "poetry"}]
                                  :description "Students write a Haiku"
                                  :length 20 ;in minutes
                                  :resources [{:sides 2
                                               :type :worksheet
                                               :format :pdf
                                               :description "Haiku Starter"
                                               :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}
                   :b {:description "Haikus 2"
                    :subject "English"
                    :year 8
                    :finished false
                    :activities [{:tags [{:text "japan"}
                                         {:text "poetry"}]
                                  :description "Students write a Haiku"
                                  :length 20 ;in minutes
                                  :resources [{:sides 2
                                               :type :worksheet
                                               :format :pdf
                                               :description "Haiku Starter"
                                               :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]}]}})

(def default-db {:active-page :calendar
                 :calendar-view :week
                 :active-week 11
                 :new-class (default-new-class-data)
                 :user {:name "Tom Hutchinson"
                        :flag :australia}
                 :planbook {:lessons test-lessons}})
