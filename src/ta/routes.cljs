(ns ta.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (re-frame/dispatch [:navigate-to :timetable]))

  (defroute "/timetable" []
    (re-frame/dispatch [:navigate-to :timetable]))

  (defroute "/planner" []
    (re-frame/dispatch [:navigate-to :planner]))

  (defroute "/timetable/:view/:id" [view week]
    (re-frame/dispatch [:view-timetable (case view
                                              "day" :day
                                              "week" :week)
                                        (js/parseInt week)]))

  (hook-browser-navigation!))
