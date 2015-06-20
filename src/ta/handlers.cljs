(ns ta.handlers
    (:require [re-frame.core :as re-frame]
              [ta.db :as db]))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/register-handler
  :navigate-to
  (fn [db [_ page]]
    (do #_(.log js/console db)
        (assoc db :active-page page))))
