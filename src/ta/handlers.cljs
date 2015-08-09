(ns ta.handlers
  (:require [ta.util :refer [colors]]
            [re-frame.core :refer [register-handler dispatch]]
            [matchbox.core :as m]
            [shodan.inspection :refer [inspect]]))

(def fb-root (m/connect "https://frederick.firebaseio.com/"))

(def fb-classes    (m/get-in fb-root [:classes]))
(def fb-lessons    (m/get-in fb-root [:lessons]))
(def fb-activities (m/get-in fb-root [:activities]))

(def starting-db
  {:user {:name "Tom Hutchinson"
          :flag :australia}
   :active-page :calendar
   :calendar-view :week
   :active-week 11
   :planbook {:open-page :lessons}
   :new-class {:color (rand-nth colors)
               :schedule {:mon   [:slot :slot :slot :slot :slot]
                          :tues  [:slot :slot :slot :slot :slot]
                          :wed   [:slot :slot :slot :slot :slot]
                          :thurs [:slot :slot :slot :slot :slot]
                          :fri   [:slot :slot :slot :slot :slot]}}})

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
                 :value (fn [[_ val]]
                          (dispatch [:update-classes val])))
    (m/listen-to fb-lessons
                 :value (fn [[_ val]]
                          (dispatch [:update-lessons val])))
    (m/listen-to fb-activities
                 :value (fn [[_ val]]
                          (dispatch [:update-activities val])))
    starting-db))

 ;; CLASSES =======================

(register-handler
 :update-new-class
 (fn [db [_ input value]]
   (assoc-in db [:new-class input] value)))

(register-handler
  :add-new-class
  (fn [db [_ _]]
    (let [class (:new-class db)]
      (m/conj! fb-classes class)
      (assoc db :new-class (:new-class starting-db))))) ;; reset form

(register-handler
  :update-classes
  (fn [db [_ classes]]
    (assoc db :classes classes)))

(register-handler
  :class
  (fn [db [_ id command attribute value]]
    (case command
      :delete (m/dissoc-in! fb-classes [id])
      :update (m/reset-in!  fb-classes [id attribute] value))
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
  :set-open-lesson
  (fn [db [_ id]]
    (assoc-in db [:planbook :open-lesson] id)))

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
  :new-empty-lesson
  (fn [db _]
    (m/conj! fb-lessons {:description "New Lesson"})
    db))

(register-handler
  :remove-lesson
  (fn [db [_ id]]
    (m/dissoc-in! fb-lessons [id])
    db))

(register-handler
  :update-lesson
  (fn [db [_ id attribute value]]
    (m/reset-in! fb-lessons [id attribute] value)
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
