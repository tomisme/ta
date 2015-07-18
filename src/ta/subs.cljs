(ns ta.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as rf]))

(rf/register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(rf/register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(rf/register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(rf/register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))

(rf/register-sub
  :new-class
  (fn [db [_ _]]
    (reaction (:new-class @db))))

(rf/register-sub
  :lessons
  (fn [db [_ page]]
    (reaction (get-in @db [:lessons page]))))

(rf/register-sub
  :timetable
  (fn [db [_ page]]
    (reaction (get-in @db [:timetable page]))))

(rf/register-sub
  :schedule
  (fn [db _]
    (reaction (:schedule @db))))
