(ns ta.core
    (:require [reagent.core :as r]
              [re-frame.core :as rf]
              [shodan.console :as console :include-macros true]
              [ta.handlers]
              [ta.subs]
              [ta.db :as db]
              [ta.routes :as routes]
              [ta.views :as views]))

(defn init []
  (r/render [views/app] (.getElementById js/document "app")))

(routes/app-routes)
(rf/dispatch-sync [:initialize-db])
(db/setup-listeners)
