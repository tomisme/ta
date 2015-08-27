(ns ^:figwheel-always ta.core
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

(render [app/app-container] (.getElementById js/document "app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

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
