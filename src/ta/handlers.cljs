(ns ta.handlers
  (:require-macros [devcards.core :as dc :refer [defcard defcard-doc deftest]])
  (:require [re-frame.core :as rf]
            [ta.util :refer [colors]]
            [matchbox.core :as m]
            [shodan.inspection :refer [inspect]]
            [json-html.core :refer [edn->hiccup]]))

(defcard "#Handlers
  ##Core logic of the application.

  * Make changes to documents (`:resource`, `:activity`, `:lesson` or `:unit`)
  * Create a beautiful printable pdf file from a document
  * Log in / out of a personal account (with firebase)
  * Make changes to personal app settings
  * Sync necessary app state (with firebase)

  Each action has an associated permission level (`:all`, `:logged-in` or `:account`)
  ")

(defcard firebase
  "`ta` is entirely client side, all personal documents are loaded into memory from
  firebase when the user logs in. Any changes to those documents are sent to firebase and
  optimistically updated in local memory.

  *Errors are not handled anywhere*."
  (dc/reagent
   (fn [data _]
     [:div {:class "ui segment"} [:i ]]))
  )

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

(rf/register-handler
  :launch-db-modal
  (fn [db _]
    (inspect db)
    #_(rf/dispatch [:launch-modal {:type :scrollbox
                               :header "app db"
                               :content (edn->hiccup (dissoc db :modal))}])
    db))

(rf/register-handler
  :inspect
  (fn [db [_ stuff]]
    (inspect stuff)
    db))

(rf/register-handler
  :fb-update
  (fn [db [_ k v]]
    (case k
      :activities (assoc-in db [:planbook :activities] v)
      :lessons    (assoc-in db [:planbook :lessons] v)
      :resources  (assoc-in db [:planbook :resources] v)
      :classes    (assoc db :classes v))))

(rf/register-handler
  :setup-db
  (fn [db _]
    (doall
      (for [{:keys [fb k]} [{:fb fb-activities :k :activities}
                            {:fb fb-classes    :k :classes}
                            {:fb fb-lessons    :k :lessons}
                            {:fb fb-resources  :k :resources}]]
        (m/listen-to fb :value (fn [[_ v]] (rf/dispatch [:fb-update k v])))))
    starting-db))

(rf/register-handler
  :class
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-classes [id])
      :update (m/reset-in!  fb-classes [id attribute] value)
      :new    (m/conj!      fb-classes (:class new-default)))
    db))


(rf/register-handler
  :activity
  (fn [db [_ command id attribute value]]
    (case command
      :delete (do (m/dissoc-in! fb-activities [id])
                  (if (= id (get-in db [:planbook :open :activity]))
                    (rf/dispatch [:set-planbook-open :activity nil])))
      :update (m/reset-in! fb-activities [id attribute] value)
      :new    (m/conj! fb-activities (:activity new-default)
                       #(rf/dispatch [:set-planbook-open :activity (ref->k %)])))
    db))

(rf/register-handler
  :resource
  (fn [db [_ command id attribute value]]
    (case command
      :delete (do (m/dissoc-in! fb-resources [id])
                  (if (= id (get-in db [:planbook :open :resource]))
                    (rf/dispatch [:set-planbook-open :resource nil])))
      :update (m/reset-in! fb-resources [id attribute] value)
      :new    (m/conj! fb-resources (:resource new-default)
                       #(rf/dispatch [:set-planbook-open :resource (ref->k %)])))
    db))

(rf/register-handler
  :add-resource-to-activity
  (fn [db [_ activity-id resource]]
    (m/conj! fb-resources resource
             #(m/conj-in! fb-activities [activity-id :resources] (ref->k %)))
    db))

(rf/register-handler
  :remove-resource-from-activity
  (fn [db [_ activity-id resource-key]]
    (m/dissoc-in! fb-activities [activity-id :resources resource-key])
    db))

(rf/register-handler
  :delete-activity-step
  (fn [db [_ activity-id step-key]]
    (m/dissoc-in! fb-activities [activity-id :steps step-key])
    db))

(rf/register-handler
  :update-activity-step
  (fn [db [_ activity-id step-key val]]
    (m/reset-in! fb-activities [activity-id :steps step-key :content] val)
    db))

(rf/register-handler
  :new-activity-step
  (fn [db [_ activity-id]]
    (let [current-steps (get-in db [:planbook :activities activity-id :steps])
          new-step {:num (inc (count current-steps))
                    :content ""}]
      (m/conj-in! fb-activities [activity-id :steps] new-step))
    db))

  ; (let [new-step    #(rf/dispatch [:new-activity-step @id])
  ;       delete-step #(rf/dispatch [:delete-activity-step @id %])
  ;       toggle-step #(rf/dispatch [:toggle-activity-step @id %])
  ;       move-step   (fn [key direction]
  ;                     (rf/dispatch [:move-activity-step @id key direction]))
  ;       update-step (fn [key event]
  ;                     (rf/dispatch [:update-activity-step @id key (e->val event)]))]

(rf/register-handler
  :toggle-activity-step
  (fn [db [_ activity-id step-key]]
    (update-in db [:planbook :activities activity-id :steps step-key :open?] not)))

(rf/register-handler
  :move-activity-step
  (fn [db [_ activity-id step-key direction]]
    (let [steps (get-in db [:planbook :activities activity-id :steps])]
      (inspect steps)
      db)))

(rf/register-handler
  :lesson
  (fn [db [_ command id attribute value]]
    (case command
      :delete (m/dissoc-in! fb-lessons [id])
      :update (m/reset-in!  fb-lessons [id attribute] value)
      :new    (m/conj!      fb-lessons (:lesson new-default)))
    db))

(rf/register-handler
  :set-planbook-open
  (fn [db [_ thing id]]
    (assoc-in db [:planbook :open thing] id)))

(rf/register-handler
  :set-planbook-filter
  (fn [db [_ filter k v]]
    (assoc-in db [:planbook :filters filter k] v)))

(rf/register-handler
  :launch-modal
  (fn [db [_ type data]]
    (assoc db :modal {:active? true :type type :data data})))

(rf/register-handler
  :close-modal
  (fn [db _]
    (assoc db :modal {:active? false})))

(rf/register-handler
  :update-modal
  (fn [db [_ k v]]
    (assoc-in db [:modal :data k] v)))

 ;; ROUTING =======================

(rf/register-handler
  :navigate-to
  (fn [db [_ page]]
    (assoc db :active-page page)))

(rf/register-handler
  :view-calendar
  (fn [db [_ view week]]
    (assoc db :active-page :calendar
              :calendar-view view
              :active-week week)))
