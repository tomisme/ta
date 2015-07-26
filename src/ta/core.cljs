(ns ta.core
    (:require [reagent.core :refer [render]]
              [re-frame.core :refer [dispatch-sync]]
              [shodan.inspection :refer [inspect]]
              [ta.handlers]
              [ta.subs]
              [ta.db :as db]
              [ta.routes :as routes]
              [ta.views :as views]))

(defn init []
  (render [views/app] (.getElementById js/document "app")))

(routes/setup-routes)
(dispatch-sync [:initialize-db])
(db/setup-listeners)
