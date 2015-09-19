(ns ta.core
  (:require-macros [secretary.core :refer [defroute]]
                   [devcards.core :as dc :refer [defcard deftest]])
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

; (defn main []
;   ;; conditionally start the app based on the presence of #main-app-area
;   ;; node is on the page
;   (if-let [node (.getElementById js/document "main-app-area")]
;     (js/React.render (sab/html [:div "This is main app is ruunning."]) node)))
;
; (main)

(defcard my-card)

(def app-node (.getElementById js/document "app"))

(defn on-js-reload []
  (if app-node (r/render [app/app-container] (.getElementById js/document "app"))))

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
