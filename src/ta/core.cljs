(ns ta.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ta.views.app :as app]
            [shodan.inspection :refer [inspect]]
            [ta.handlers]
            [ta.subs]))

(defn on-js-reload []
  (r/render [app/app-container] (.getElementById js/document "app")))

(on-js-reload)

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
  (rf/dispatch [:view-calendar (case view "day" :day
                                       "week" :week)
                            (js/parseInt id)]))

(doto (History.)
  (events/listen EventType/NAVIGATE
                 (fn [event] (secretary/dispatch! (.-token event))))
  (.setEnabled true))

(rf/dispatch-sync [:setup-db])
