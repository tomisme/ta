(ns ta.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [ta.util :refer [weekdays]]
              [shodan.inspection :refer [inspect]]
              [re-frame.core :refer [register-sub]]))

(register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))

(register-sub
  :new-class
  (fn [db [_ _]]
    (reaction (:new-class @db))))

(register-sub
  :lessons
  (fn [db [_ page]]
    (reaction (get-in @db [:lessons page]))))

(register-sub
  :timetable
  (fn [db [_ page]]
    (reaction (get-in @db [:timetable page]))))

(defn get-class-or-dot [classes-in-slot]
  (some #(if (not= :empty %) % :dot) classes-in-slot))

;; TODO: do something about class slot conflicts
;;       currently returns first class found for the slot or :dot
(defn class-in-slot [classes day session]
  (do (inspect "================================")
  (get-class-or-dot (for [class classes]
    (do (inspect "internal seq")
        (inspect (first class))
        (inspect day)
        (inspect session)
        (inspect (get-in class [:schedule day session]))
        (inspect (get-in (second class) [:schedule day])))
    #_(if (= :selected (get-in class [:schedule day session]))
      id :empty)))))

(defn classes->schedule [classes]
  (zipmap weekdays (for [weekday (range 5)]
                     (into [] (for [cell (range 5)]
                       (if (seq classes) ;; if there are no classes
                         (do #_(inspect (class-in-slot classes (get weekdays weekday) cell))
                         (class-in-slot classes (get weekdays weekday) cell))
                         :dot) ;; return a grid of dot periods
                       #_(str (get weekdays weekday) cell))))))

(register-sub
  :schedule
  (fn [db _]
    (reaction (classes->schedule (:classes @db))
     #_(:schedule @db))))
