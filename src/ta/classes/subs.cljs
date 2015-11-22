(ns ta.classes.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [ta.util :refer [weekdays]]))

(defn class-in-slot
  "The id keyword of the first class found for the slot or nil"
  [classes day session]
  (some #(if % %) (for [[id class] classes]
                    (if (= :selected (get-in class [:schedule day session])) id))))

(defn classes->schedule
  [classes]
  "A matrix of combined class schedules"
  (zipmap weekdays
    (for [day weekdays]
      (into [] (for [session (range 5)]
                 (if (seq classes) (class-in-slot classes day session)))))))

(rf/register-sub
  :schedule
  (fn [db _]
    (reaction (classes->schedule (:classes @db)))))

(rf/register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))
