(ns ta.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :username
  (fn [db]
    (reaction (:username @db))))

(re-frame/register-sub
  :active-page
  (fn [db]
    (reaction (:active-page @db))))
