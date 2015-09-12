(ns ta.handlers
  (:require [ta.util :refer [colors]]
            [re-frame.core :as r]
            [matchbox.core :as m]
            [shodan.inspection :refer [inspect]]
            [json-html.core :refer [edn->hiccup]]))

(defn ref->k [ref] (keyword (m/key ref)))

(def fb-root (m/connect "https://frederick.firebaseio.com/"))

(def fb-classes    (m/get-in fb-root [:classes]))
(def fb-lessons    (m/get-in fb-root [:lessons]))
(def fb-activities (m/get-in fb-root [:activities]))
(def fb-units      (m/get-in fb-root [:tags]))
(def fb-resources  (m/get-in fb-root [:resources]))
(def fb-tags       (m/get-in fb-root [:tags]))

(def starting-db {:user {:name "Tom Hutchinson"
                         :flag :australia}
                  :active-page :calendar
                  :calendar-view :week
                  :active-week 11
                  :planbook {:open {:tab :lessons}}})

(def test-url "readwritethink.org/files/resources/printouts/30697_haiku.pdf")

(def new-default
  {:activity {:tags [{:text "english"}
                     {:text "poetry"}
                     {:text "8s"}
                     {:text "9s"}]
              :description "Students write several Haikus using a starter sheet"
              :length 30}
   :class    {:name "New Class"
              :editing? true
              :color (rand-nth colors)
              :schedule {:mon   [:slot :slot :slot :slot :slot]
                         :tues  [:slot :slot :slot :slot :slot]
                         :wed   [:slot :slot :slot :slot :slot]
                         :thurs [:slot :slot :slot :slot :slot]
                         :fri   [:slot :slot :slot :slot :slot]}}
   :lesson   {:description "New Lesson"}})

(r/register-handler
  :launch-db-modal
  (fn [db _]
    (inspect db)
    #_(r/dispatch [:launch-modal {:type :scrollbox
                               :header "app db"
                               :content (edn->hiccup (dissoc db :modal))}])
    db))

(r/register-handler
  :inspect
  (fn [db [_ stuff]]
    (inspect stuff)
    db))

(r/register-handler
  :fb-update
  (fn [db [_ k v]]
    (case k
      :activities (assoc-in db [:planbook :activities] v)
      :lessons    (assoc-in db [:planbook :lessons] v)
      :resources  (assoc-in db [:planbook :resources] v)
      :classes    (assoc db :classes v))))

(r/register-handler
  :setup-db
  (fn [db _]
    (doall
      (for [{:keys [fb k]} [{:fb fb-activities :k :activities}
                            {:fb fb-classes    :k :classes}
                            {:fb fb-lessons    :k :lessons}
                            {:fb fb-resources  :k :resources}]]
        (m/listen-to fb :value (fn [[_ v]] (r/dispatch [:fb-update k v])))))
    starting-db))

(r/register-handler
  :class
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-classes [id])
      :update (m/reset-in!  fb-classes [id attribute] value)
      :new    (m/conj!      fb-classes (:class new-default)))
    db))


(r/register-handler
  :activity
  (fn [db [_ command id attribute value]]
    (case command
      :delete (do (m/dissoc-in! fb-activities [id])
                  (if (= id (get-in db [:planbook :open :activity]))
                    (r/dispatch [:set-planbook-open :activity nil])))
      :update (m/reset-in! fb-activities [id attribute] value)
      :new    (m/conj! fb-activities (:activity new-default)
                       #(r/dispatch [:set-planbook-open :activity (ref->k %)])))
    db))

(r/register-handler
  :resource
  (fn [db [_ command id attribute value]]
    (case command
      :delete (do (m/dissoc-in! fb-resources [id])
                  (if (= id (get-in db [:planbook :open :resource]))
                    (r/dispatch [:set-planbook-open :resource nil])))
      :update (m/reset-in! fb-resources [id attribute] value)
      :new    (m/conj! fb-resources (:resource new-default)
                       #(r/dispatch [:set-planbook-open :resource (ref->k %)])))
    db))

(r/register-handler
  :add-resource-to-activity
  (fn [db [_ id resource]]
    (m/conj! fb-resources resource
             #(m/conj-in! fb-activities [id :resources] (ref->k %)))
    db))

(r/register-handler
  :remove-resource-from-activity
  (fn [db [_ id resource-key]]
    (m/dissoc-in! fb-activities [id :resources resource-key])
    db))

(r/register-handler
  :delete-activity-step
  (fn [db [_ id step-key]]
    (m/dissoc-in! fb-activities [id :steps step-key])
    db))

(r/register-handler
  :new-activity-step
  (fn [db [_ activity-id]]
    (let [current-steps (get-in db [:planbook :activities activity-id :steps])
          new-step {:num (inc (count current-steps))
                    :content "New step!"}]
      (m/conj-in! fb-activities [activity-id :steps] new-step)
      db)))

(r/register-handler
  :lesson
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-lessons [id])
      :update (m/reset-in!  fb-lessons [id attribute] value)
      :new    (m/conj!      fb-lessons (:lesson new-default)))
    db))

(r/register-handler
  :set-planbook-open
  (fn [db [_ thing id]]
    (assoc-in db [:planbook :open thing] id)))

(r/register-handler
  :set-planbook-filter
  (fn [db [_ filter k v]]
    (assoc-in db [:planbook :filters filter k] v)))

(r/register-handler
  :launch-modal
  (fn [db [_ type data]]
    (assoc db :modal {:active? true :type type :data data})))

(r/register-handler
  :close-modal
  (fn [db _]
    (assoc db :modal {:active? false})))

(r/register-handler
  :update-modal
  (fn [db [_ k v]]
    (assoc-in db [:modal :data k] v)))

 ;; ROUTING =======================

(r/register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(r/register-handler
  :view-calendar
  (fn [db [_ view week]]
    (assoc db :active-page :calendar
              :calendar-view view
              :active-week week)))
