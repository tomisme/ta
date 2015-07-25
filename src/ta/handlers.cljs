(ns ta.handlers
    (:require [re-frame.core :refer [register-handler]]
              [shodan.inspection :refer [inspect]]
              [ta.db :as db]))

(register-handler
  :inspect
  (fn [db [_ stuff]]
    (inspect stuff)
    db))

(register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(register-handler
 :update-new-class
 (fn [db [_ input value]]
   (assoc-in db [:new-class input] value)))

(register-handler
  :add-new-class!
  (fn [db [_ _]]
    (let [class (:new-class db)]
      (db/add-new-class! class)
      (assoc db :new-class (db/default-new-class-data))))) ;; reset form

(register-handler
  :update-classes
  (fn [db [_ classes]]
    (assoc db :classes classes)))

(register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(register-handler
  :view-timetable
  (fn [db [_ view week]]
    (assoc db :active-page :timetable
              :timetable-view view
              :active-week week)))
