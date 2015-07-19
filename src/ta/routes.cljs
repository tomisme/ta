(ns ta.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [re-frame.core :refer [dispatch]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen EventType/NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (dispatch [:navigate-to :timetable]))

  (defroute "/timetable" []
    (dispatch [:navigate-to :timetable]))

  (defroute "/planner" []
    (dispatch [:navigate-to :planner]))

  (defroute "/classes" []
    (dispatch [:navigate-to :classes]))

  (defroute "/timetable/:view/:id" [view id]
    (dispatch [:view-timetable (case view "day" :day
                                          "week" :week)
                               (js/parseInt id)]))

  (hook-browser-navigation!))
