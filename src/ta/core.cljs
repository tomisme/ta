(ns ta.core
    (:require [reagent.core :refer [render]]
              [re-frame.core :refer [dispatch-sync]]
              [shodan.inspection :refer [inspect]]
              [ta.handlers]
              [ta.subs]
              [ta.db :as db]
              [ta.routes :as routes]
              [ta.views.app :as app]))

(defn init []
  (render [app/container] (.getElementById js/document "app")))

(routes/setup-routes)
(dispatch-sync [:initialize-db])
(db/setup-listeners)
