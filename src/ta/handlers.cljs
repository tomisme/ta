(ns ta.handlers
  (:require [ta.util :refer [colors]]
            [re-frame.core :refer [register-handler dispatch]]
            [matchbox.core :as m]
            [shodan.inspection :refer [inspect]]
            [json-html.core :refer [edn->hiccup]]))

(def fb-root (m/connect "https://frederick.firebaseio.com/"))

(def fb-classes    (m/get-in fb-root [:classes]))
(def fb-lessons    (m/get-in fb-root [:lessons]))
(def fb-activities (m/get-in fb-root [:activities]))

(def starting-db {:user {:name "Tom Hutchinson"
                         :flag :australia}
                  :active-page :calendar
                  :calendar-view :week
                  :active-week 11
                  :planbook {:open {:tab :lessons}}})

(def test-url "readwritethink.org/files/resources/printouts/30697_haiku.pdf")

(def new-plan-of-category
  {:activity {:tags [{:text "english"}
                     {:text "poetry"}
                     {:text "8s"}
                     {:text "9s"}]
              :description "Students write several Haikus using a starter sheet"
              :length 30 ;in minutes
              :resources [{:sides 2
                           :type :worksheet
                           :format :pdf
                           :description "Haiku Starter"
                           :url test-url}]}
   :class    {:name "New Class"
              :editing? true
              :color (rand-nth colors)
              :schedule {:mon   [:slot :slot :slot :slot :slot]
                         :tues  [:slot :slot :slot :slot :slot]
                         :wed   [:slot :slot :slot :slot :slot]
                         :thurs [:slot :slot :slot :slot :slot]
                         :fri   [:slot :slot :slot :slot :slot]}}
   :lesson   {:description "New Lesson"}})

(register-handler
  :launch-db-modal
  (fn [db _]
    (dispatch [:modal :launch {:scrollbox? true
                               :text (edn->hiccup (dissoc db :modal))}])
    db))

(register-handler
  :inspect
  (fn [db [_ stuff]]
    (inspect stuff)
    db))

(register-handler
  :fb-update
  (fn [db [_ k v]]
    (case k
      :activities (assoc-in db [:planbook :activities] v)
      :lessons    (assoc-in db [:planbook :lessons] v)
      :classes    (assoc db :classes v))))

(register-handler
  :setup-db
  (fn [db _]
    (doall
      (for [{:keys [fb k]} [{:fb fb-activities :k :activities}
                            {:fb fb-classes    :k :classes}
                            {:fb fb-lessons    :k :lessons}]]
        (m/listen-to fb :value (fn [[_ v]] (dispatch [:fb-update k v])))))
    starting-db))

(register-handler
  :class
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-classes [id])
      :update (m/reset-in!  fb-classes [id attribute] value)
      :new    (m/conj!      fb-classes (:class new-plan-of-category)))
    db))

(register-handler
  :activity
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-activities [id])
      :update (m/reset-in!  fb-activities [id attribute] value)
      :new    (m/conj!      fb-activities (:activity new-plan-of-category)))
    db))

(register-handler
  :lesson
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-lessons [id])
      :update (m/reset-in!  fb-lessons [id attribute] value)
      :new    (m/conj!      fb-lessons (:lesson new-plan-of-category)))
    db))

(register-handler
  :set-open
  (fn [db [_ thing id]]
    (assoc-in db [:planbook :open thing] id)))

(register-handler
  :set-filter
  (fn [db [_ filter k v]]
    (assoc-in db [:planbook :filters filter k] v)))

(register-handler
  :modal
  (fn [db [_ action model-data]]
    (case action
      :launch (-> db
                (assoc-in [:modal :data] model-data)
                (assoc-in [:modal :active?] true))
      :close (assoc-in db [:modal :active?] false))))

 ;; ROUTING =======================

(register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(register-handler
  :view-calendar
  (fn [db [_ view week]]
    (assoc db :active-page :calendar
              :calendar-view view
              :active-week week)))
