(ns ta.core
  (:require-macros [secretary.core :refer [defroute]]
                   [devcards.core :as dc :refer [defcard defcard-doc deftest]])
  (:import goog.History)
  (:require [re-frame.core :as rf]
            [reagent.core :as rg]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [shodan.inspection :refer [inspect]]
            [ta.view :as view]
            [ta.handlers]
            [ta.subs]))

(defcard "#ta
  ##Development Lab

  This is the developer playground and testing environment for the `ta` application.

  `ta` is a tool for teachers. That makes this a tool for developers of tools
  for teachers.")

(secretary/set-config! :prefix "#")

(defroute "/" []
  (rf/dispatch [:navigate-to :calendar]))

(defroute "/calendar" []
  (rf/dispatch [:navigate-to :calendar]))

(defroute "/planbook" []
  (rf/dispatch [:navigate-to :planbook]))

(defroute "/classes" []
  (rf/dispatch [:navigate-to :classes]))

(defroute "/calendar/:view/:id" [view id]
  (rf/dispatch [:view-calendar (case view "day" :day "week" :week) (js/parseInt id)]))

(doto (History.)
  (events/listen EventType/NAVIGATE
                 (fn [event] (secretary/dispatch! (.-token event))))
  (.setEnabled true))

(rf/dispatch-sync [:setup-db])

(defn render-app []
  (if (.getElementById js/document "app")
    (rg/render [view/container] (.getElementById js/document "app"))))

(render-app)
