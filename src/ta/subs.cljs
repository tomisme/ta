(ns ta.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))

(register-sub
  :new-class
  (fn [db [_ _]]
    (reaction (:new-class @db))))

(register-sub
  :lessons
  (fn [db [_ page]]
    (reaction (get-in @db [:lessons page]))))

(register-sub
  :timetable
  (fn [db [_ page]]
    (reaction (get-in @db [:timetable page]))))

(register-sub
  :schedule
  (fn [db _]
    (reaction (:schedule @db))))
