(ns ta.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.util :refer [weekdays]]
            [shodan.inspection :refer [inspect]]
            [re-frame.core :as r]))

(r/register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(r/register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(r/register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(r/register-sub
  :modal
  (fn [db [_ option]]
    (case option
      :active? (reaction (get-in @db [:modal :active?]))
      :data    (reaction (get-in @db [:modal :data]))
      :type    (reaction (get-in @db [:modal :type])))))

(defn filtered-lessons
  [lessons filters]
  (filter (fn [[id lesson]]
            (if (contains? filters :finished)
              (= (:finished lesson) (:finished filters))
              true))
          lessons))

(r/register-sub
  :lessons
  (fn [db [_ type]]
    (if (= :filtered type)
      (let [filters  (r/subscribe [:filter :lessons])]
        (reaction (filtered-lessons (get-in @db [:planbook :lessons]) @filters)))
      (reaction (get-in @db [:planbook :lessons])))))

(r/register-sub
  :lesson
  (fn [db [_ id]]
    (let [lessons (r/subscribe [:lessons])]
      (reaction (get @lessons id)))))

(r/register-sub
  :activities
  (fn [db _]
    (reaction (get-in @db [:planbook :activities]))))

(r/register-sub
  :activity
  (fn [db [_ id]]
    (let [activities (r/subscribe [:activities])]
      (reaction (get @activities id)))))

(r/register-sub
  :activity-steps
  (fn [db [_ id]]
    (reaction (sort-by :num (get-in @db [:planbook :activities id :steps])))))

(r/register-sub
  :lesson-activities
  (fn [db [_ lesson-id]]
    (let [activities @(r/subscribe [:activities])
          lessons    (r/subscribe [:lessons])
          lesson     (reaction (get @lessons lesson-id))
          ids        (reaction (get @lesson :activity-ids))]
      (reaction (for [id @ids]
                  [id (get activities id)])))))

(r/register-sub
  :resources
  (fn [db _]
    (reaction (get-in @db [:planbook :resources]))))

(r/register-sub
  :resource
  (fn [db [_ id]]
    (let [resources (r/subscribe [:resources])]
      (reaction (get @resources id)))))

(r/register-sub
  :open
  (fn [db [_ thing]]
    (reaction (get-in @db [:planbook :open thing]))))

(r/register-sub
  :filter
  (fn [db [_ filter]]
    (reaction (get-in @db [:planbook :filters filter]))))

(r/register-sub
  :classes
  (fn [db _]
    (reaction (:classes @db))))

(defn class-in-slot
  "The id keyword of the first class found for the slot or nil"
  [classes day session]
  (some #(if % %) (for [[id class] classes]
                    (if (= :selected (get-in class [:schedule day session])) id))))

(defn classes->schedule
  [classes]
  "A matrix of combined class schedules"
  (zipmap weekdays (for [day weekdays]
                     (into [] (for [session (range 5)]
                       (if (seq classes)
                         (class-in-slot classes day session)))))))

(r/register-sub
  :schedule
  (fn [db _]
    (reaction (classes->schedule (:classes @db)))))
