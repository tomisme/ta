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

;; TODO work out why filter won't work inside a reaction
(register-sub
  :lessons
  (fn [db [_ type]]
    (if (= :filtered type)
      (let [filter (subscribe [:filter :lessons])]
        (reaction (filter (fn [lesson]
                            (= (:finished filter) (:finished lesson)))
                          (get-in @db [:planbook :lessons]))))
      (reaction (get-in @db [:planbook :lessons])))))

(register-sub
  :lesson
  (fn [db [_ id]]
    (let [lessons (subscribe [:lessons])]
      (reaction (get @lessons id)))))

(register-sub
  :activities
  (fn [db _]
    (reaction (get-in @db [:planbook :activities]))))

(register-sub
  :activity
  (fn [db [_ id]]
    (let [activities (subscribe [:activities])]
      (reaction (get @activities id)))))

(register-sub
  :lesson-activities
  (fn [db [_ lesson-id]]
    (let [activities @(subscribe [:activities])
          lessons    (subscribe [:lessons])
          lesson     (reaction (get @lessons lesson-id))
          ids        (reaction (get @lesson :activity-ids))]
      (reaction (for [id @ids]
                  [id (get activities id)])))))

(register-sub
  :open
  (fn [db [_ thing]]
    (reaction (get-in @db [:planbook :open thing]))))

(register-sub
  :filter
  (fn [db [_ filter]]
    (reaction (get-in @db [:planbook :filters filter]))))

(register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))

(defn class-in-slot
  "The id keyword of the first class found for the slot or nil"
  [classes day session]
  (some #(if % %) ;; get the first truthy value (a filled slot)
        (for [[id class] classes]
          (if (= :selected (get-in class [:schedule day session])) id))))

(defn classes->schedule
  [classes]
  "A matrix of combined class schedules"
  (zipmap weekdays (for [day weekdays]
                     (into [] (for [session (range 5)]
                       (if (seq classes)
                         (class-in-slot classes day session)))))))

(register-sub
  :schedule
  (fn [db _]
    (reaction (classes->schedule (:classes @db)))))
