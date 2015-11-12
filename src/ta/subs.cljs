(ns ta.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))

(rf/register-sub
  :user
  (fn [db]
    (reaction (:user @db))))

(rf/register-sub
  :active-page
  (fn [db _]
    (reaction (:active-page @db))))

(rf/register-sub
  :active-week
  (fn [db _]
    (reaction (:active-week @db))))

(rf/register-sub
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

(rf/register-sub
  :lessons
  (fn [db [_ type]]
    (if (= :filtered type)
      (let [filters  (rf/subscribe [:filter :lessons])]
        (reaction (filtered-lessons (get-in @db [:planbook :lessons]) @filters)))
      (reaction (get-in @db [:planbook :lessons])))))

(rf/register-sub
  :lesson
  (fn [db [_ id]]
    (let [lessons (rf/subscribe [:lessons])]
      (reaction (get @lessons id)))))

(rf/register-sub
  :activities
  (fn [db _]
    (reaction (get-in @db [:planbook :activities]))))

(rf/register-sub
  :activity
  (fn [db [_ id]]
    (let [activities (rf/subscribe [:activities])]
      (reaction (get @activities id)))))

(rf/register-sub
  :activity-steps
  (fn [db [_ id]]
    (reaction (sort-by :num (get-in @db [:planbook :activities id :steps])))))

(rf/register-sub
  :lesson-activities
  (fn [db [_ lesson-id]]
    (let [activities @(rf/subscribe [:activities])
          lessons    (rf/subscribe [:lessons])
          lesson     (reaction (get @lessons lesson-id))
          ids        (reaction (get @lesson :activity-ids))]
      (reaction (for [id @ids]
                  [id (get activities id)])))))

(rf/register-sub
  :resources
  (fn [db _]
    (reaction (get-in @db [:planbook :resources]))))

(rf/register-sub
  :resource
  (fn [db [_ id]]
    (let [resources (rf/subscribe [:resources])]
      (reaction (get @resources id)))))

(rf/register-sub
  :open
  (fn [db [_ thing]]
    (reaction (get-in @db [:planbook :open thing]))))

(rf/register-sub
  :filter
  (fn [db [_ filter]]
    (reaction (get-in @db [:planbook :filters filter]))))
