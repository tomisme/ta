(ns ta.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [reagent.core :refer [render]]
            [re-frame.core :as r]
            [shodan.inspection :refer [inspect]]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ta.views.app :as app]
            [ta.handlers]
            [ta.subs] ))

(defn on-js-reload []
  (render [app/app-container] (.getElementById js/document "app")))

(on-js-reload)

(secretary/set-config! :prefix "#")

(defroute "/" []
  (r/dispatch [:navigate-to :calendar]))

(defroute "/calendar" []
  (r/dispatch [:navigate-to :calendar]))

(defroute "/planbook" []
  (r/dispatch [:navigate-to :planbook]))

(defroute "/classes" []
  (r/dispatch [:navigate-to :classes]))

(defroute "/calendar/:view/:id" [view id]
  (r/dispatch [:view-calendar (case view "day" :day
                                       "week" :week)
                            (js/parseInt id)]))

(doto (History.)
  (events/listen EventType/NAVIGATE
                 (fn [event] (secretary/dispatch! (.-token event))))
  (.setEnabled true))

(r/dispatch-sync [:setup-db])
