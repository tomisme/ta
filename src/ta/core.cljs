(ns ta.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [ta.handlers]
              [ta.subs]
              [ta.db :as db]
              [ta.routes :as routes]
              [ta.views :as views]))

(defn init []
  (reagent/render [views/app]
                  (.getElementById js/document "app")))

(routes/app-routes)
(re-frame/dispatch-sync [:initialize-db])
(db/setup-fb-listener)
