(ns ta.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [ta.handlers]
              [ta.subs]
              [ta.routes :as routes]
              [ta.views :as views]))

(defn mount-root []
  (reagent/render [views/app]
                  (.getElementById js/document "app")))

(defn init []
  (do (routes/app-routes)
      (re-frame/dispatch-sync [:initialize-db])
      (mount-root)
      (.log js/console "Init done BOOOYA")))

;(defn init []
;  (.log js.console "AHHH"))
;(routes/app-routes)
;(re-frame/dispatch-sync [:initialize-db])
;(mount-root)
