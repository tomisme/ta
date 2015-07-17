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
  :fb-update
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
