(ns ta.classes.handlers
  (:require-macros [devcards.core :as dc :refer [defcard defcard-doc deftest]])
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [matchbox.core :as m]
            [ta.handlers :as handlers]
            [ta.util :refer [colors]]))

(def fb-classes (m/get-in handlers/fb-root [:classes]))

(defn listen-to-fb-classes
  []
  (m/listen-to fb-classes :value (fn [[_ v]] (rf/dispatch [:fb-update-classes v]))))

(def new-class-data {:name "New Class"
                     :editing? true
                     :color (rand-nth colors)
                     :schedule {:mon   [:slot :slot :slot :slot :slot]
                                :tues  [:slot :slot :slot :slot :slot]
                                :wed   [:slot :slot :slot :slot :slot]
                                :thurs [:slot :slot :slot :slot :slot]
                                :fri   [:slot :slot :slot :slot :slot]}})

(rf/register-handler
  :fb-update-classes
  (fn [db [_ v]]
    (assoc db :classes v)))

(rf/register-handler
  :delete-class
  (fn [db [_ id]]
    (m/dissoc-in! fb-classes [id])
    db))

(rf/register-handler
  :update-class
  (fn [db [_ id k v]]
    (m/reset-in! fb-classes [id k] v)
    db))

(rf/register-handler
  :new-class
  (fn [db _]
    (m/conj! fb-classes new-class-data)
    db))
