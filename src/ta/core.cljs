(ns ta.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [reagent.core :refer [render]]
            [re-frame.core :refer [dispatch dispatch-sync]]
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
  (dispatch [:navigate-to :calendar]))

(defroute "/calendar" []
  (dispatch [:navigate-to :calendar]))

(defroute "/planbook" []
  (dispatch [:navigate-to :planbook]))

(defroute "/classes" []
  (dispatch [:navigate-to :classes]))

(defroute "/calendar/:view/:id" [view id]
  (dispatch [:view-calendar (case view "day" :day
                                       "week" :week)
                            (js/parseInt id)]))

(doto (History.)
  (events/listen EventType/NAVIGATE
                 (fn [event] (secretary/dispatch! (.-token event))))
  (.setEnabled true))

(dispatch-sync [:setup-db])
