(ns ta.handlers
    (:require [re-frame.core :as re-frame]
              [shodan.console :as console :include-macros true]
              [shodan.inspection :refer [inspect]]
              [ta.db :as db]))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/register-handler
  :update-new-class-name
  (fn [db [_ name]]
    (assoc-in db [:new-class :name] name)))

(re-frame/register-handler
  :update-new-class-color
  (fn [db [_ color]]
    (assoc-in db [:new-class :color] color)))

(re-frame/register-handler
  :add-new-class
  (fn [db [_ _]]
    (let [class (:new-class db)]
      #_(inspect (conj (:classes db) class))
      (db/add-new-class class)
      db)))

(re-frame/register-handler
  :update-classes
  (fn [db [_ data]]
    (assoc db :classes data)))

(re-frame/register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(re-frame/register-handler
  :view-timetable
  (fn [db [_ view week]]
      ;TODO insert pre condition that week is a number or generalised type checking?
    (assoc db :active-page :timetable
              :timetable-view view
              :active-week week)))
