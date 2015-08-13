(ns ta.handlers
  (:require [ta.util :refer [colors]]
            [re-frame.core :refer [register-handler dispatch]]
            [matchbox.core :as m]
            [shodan.inspection :refer [inspect]]))

(def fb-root (m/connect "https://frederick.firebaseio.com/"))

(def fb-classes    (m/get-in fb-root [:classes]))
(def fb-lessons    (m/get-in fb-root [:lessons]))
(def fb-activities (m/get-in fb-root [:activities]))

(def starting-db {:user {:name "Tom Hutchinson"
                         :flag :australia}
                  :active-page :calendar
                  :calendar-view :week
                  :active-week 11
                  :planbook {:open {:page :lessons}}})

(def new-class {:name "New Class"
                :editing? true
                :color (rand-nth colors)
                :schedule {:mon   [:slot :slot :slot :slot :slot]
                           :tues  [:slot :slot :slot :slot :slot]
                           :wed   [:slot :slot :slot :slot :slot]
                           :thurs [:slot :slot :slot :slot :slot]
                           :fri   [:slot :slot :slot :slot :slot]}})

(def new-lesson {:description "New Lesson"})

(def test-activity
  {:tags [{:text "english"}
          {:text "poetry"}
          {:text "8s"}
          {:text "9s"}]
   :description "Students write several Haikus using a starter sheet"
   :length 30 ;in minutes
   :resources [{:sides 2
                :type :worksheet
                :format :pdf
                :description "Haiku Starter"
                :url test-url}]})

(register-handler
  :inspect-db
  (fn [db _]
    (inspect db)
    db))

(register-handler
  :inspect
  (fn [db [_ stuff]]
    (inspect stuff)
    db))

(register-handler
  :setup-db
  (fn [db _]
    (m/listen-to fb-classes
                 :value (fn [[_ val]] (dispatch [:update-classes val])))
    (m/listen-to fb-lessons
                 :value (fn [[_ val]] (dispatch [:update-lessons val])))
    (m/listen-to fb-activities
                 :value (fn [[_ val]] (dispatch [:update-activities val])))
    starting-db))

 ;; CLASSES =======================

(register-handler
  :update-classes
  (fn [db [_ classes]]
    (assoc db :classes classes)))

(register-handler
  :class
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-classes [id])
      :update (m/reset-in!  fb-classes [id attribute] value)
      :new    (m/conj! fb-classes new-class))
    db))

 ;; PLANBOOK =======================

(register-handler
  :update-lessons
  (fn [db [_ lessons]]
    (assoc-in db [:planbook :lessons] lessons)))

(register-handler
  :update-activities
  (fn [db [_ activities]]
    (assoc-in db [:planbook :activities] activities)))

(register-handler
  :set-open
  (fn [db [_ thing id]]
    (assoc-in db [:planbook :open thing] id)))

(register-handler
  :set-planbook-page
  (fn [db [_ page]]
    (assoc-in db [:planbook :open-page] page)))

(register-handler
  :new-activity
  (fn [db [_ activity]]
    (m/conj! fb-activities activity)
    db))

(register-handler
  :activity
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-activities [id])
      :update (m/reset-in!  fb-activities [id attribute] value)
      :new    (m/conj!      fb-activities test-activity))
    db))

(register-handler
  :lesson
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-lessons [id])
      :update (m/reset-in!  fb-lessons [id attribute] value)
      :new    (m/conj!      fb-lessons new-lesson))
    db))

 ;; ROUTING =======================

(register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(register-handler
  :view-calendar
  (fn [db [_ view week]]
    (assoc db :active-page :calendar
              :calendar-view view
              :active-week week)))
