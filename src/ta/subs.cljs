(ns ta.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(re-frame/register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(re-frame/register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(re-frame/register-sub
  :lessons
  (fn [db [_ page]]
    (reaction (get-in @db [:lessons page]))))

(re-frame/register-sub
  :timetable
  (fn [db [_ page]]
    (reaction (get-in @db [:timetable page]))))
