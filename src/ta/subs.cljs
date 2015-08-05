(ns ta.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.util :refer [weekdays]]
            [shodan.inspection :refer [inspect]]
            [re-frame.core :refer [register-sub subscribe]]))

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
  (fn [db _]
    (reaction (:new-class @db))))

(register-sub
  :planbook-page
  (fn [db _]
    (reaction (get-in @db [:planbook :open-page]))))

(register-sub
  :lessons
  (fn [db _]
    (reaction (get-in @db [:planbook :lessons]))))

(register-sub
  :activities
  (fn [db _]
    (reaction (get-in @db [:planbook :activities]))))

(register-sub
  :lesson-activities
  (fn [db [_ lesson-id]]
    (reaction (let [activities (subscribe [:activities])
                    lessons    (subscribe [:lessons])
                    lesson     (reaction (get @lessons lesson-id))
                    ids        (reaction (get @lesson :activity-ids))]
                (for [id @ids]
                  [id (get @activities id)])))))

(register-sub
  :open-lesson
  (fn [db _]
    (reaction (get-in @db [:planbook :open-lesson]))))

(register-sub
  :calendar
  (fn [db [_ page]]
    (reaction (get-in @db [:calendar page]))))

(defn class-in-slot [classes day session]
  "The id keyword of the first class found for the slot or nil"
  ;; TODO: handle class clashes
  (some #(if % %) ;; get the first truthy value (a filled slot)
        (for [[id class] classes]
          (if (= :selected (get-in class [:schedule day session])) id))))

(defn classes->schedule [classes]
  "A matrix of combined class schedules"
  (zipmap weekdays (for [day weekdays]
                     (into [] (for [session (range 5)]
                       (if (seq classes)
                         (class-in-slot classes day session)))))))

(register-sub
  :schedule
  (fn [db _]
    (reaction (classes->schedule (:classes @db)))))
