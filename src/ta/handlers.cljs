(ns ta.handlers
    (:require [re-frame.core :refer [register-handler]]
              [shodan.inspection :refer [inspect]]
              [ta.db :as db]))

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
  :initialize-db
  (fn [_ _]
    db/default-db))

 ;; CLASSES =======================

(register-handler
 :update-new-class
 (fn [db [_ input value]]
   (assoc-in db [:new-class input] value)))

(register-handler
  :add-new-class
  (fn [db [_ _]]
    (let [class (:new-class db)]
      (db/add-new-class! class)
      (assoc db :new-class (db/default-new-class-data))))) ;; reset form

(register-handler
  :update-classes
  (fn [db [_ classes]]
    (assoc db :classes classes)))

 ;; PLANBOOK =======================

(register-handler
  :update-lessons
  (fn [db [_ lessons]]
    (assoc-in db [:planbook :lessons] lessons)))

(register-handler
  :add-lesson
  (fn [db _]
    (db/add-lesson! {:description "New Lesson"})
    #_(assoc db :planbook :adding-lesson true) ;; TODO: make new lesson button spin?
    db))

(register-handler
  :update-lesson
  (fn [db [_ id attribute value]]
    (db/update-lesson-attribute! id attribute value)
    db))

(register-handler
  :set-open-lesson
  (fn [db [_ id]]
    (assoc-in db [:planbook :open-lesson] id)))

(register-handler
  :set-planbook-page
  (fn [db [_ page]]
    (assoc-in db [:planbook :open-page] page)))

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
