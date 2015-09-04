(ns ta.views.modal
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [shodan.inspection :refer [inspect]]
            [ta.views.common :refer [sem icon]]
            [re-frame.core :refer [subscribe dispatch]]))

#_{:active? true
   :type :add-resource
   :data {:url "..."}}

(defn dimmer
  [{:keys [active?]}]
  [:div {:class (sem "ui dimmer #_modals page #_transition #_visible"
                     (if active? "active"))}])

(defn modal
  [{:keys [active? type] :as arg-map}]
  (let [do-and-close (fn [thing-to-do] (thing-to-do)
                                       (dispatch [:modal :close]))]
    [:div {:class (sem "ui modal transition #_active"
                       (if active? "visible"))
           :style {:top "10%"}}
      [:i {:class "close icon"
           :on-click #(dispatch [:modal :close])}]
      (case type
        :confirm [:div {:class "header"} "Confirm"]
        [:div {:class "header"} (:header arg-map)])
      (case type
        :scrollbox [:div {:class "content"}
                     [:div {:style {:overflow-y "auto"
                                    :max-height "500px"}} (:content arg-map)]]
        :confirm   [:div {:class "image content"}
                     [:div {:class "image"} (icon (:i arg-map))]
                     [:div {:class "description"}
                       [:p (:question arg-map)]]]
        [:div {:class "content"} (get arg-map :content)])
      (case type
        :confirm [:div {:class "actions"}
                   [:div {:class "ui button"
                          :on-click #(do-and-close (:on-yes arg-map))}
                     "Yes, I'm sure"]]
        :form    [:div {:class "actions"}
                   [:div {:class "ui green button"
                          :on-click #(do-and-close (:on-submit arg-map))}
                     (:submit-text arg-map)]]
        nil)]))

(defn global-modal
  []
  (let [active? (subscribe [:modal :active?])
        data    (subscribe [:modal :data])
        type    (subscribe [:modal :type])]
    (fn []
      (case @type
        :delete-activity [modal {:active? @active?
                                 :type :confirm
                                 :i "trash"
                                 :question "Are you sure you want to delete this activity?"
                                 :on-yes #(dispatch [:activity :delete (:id @data)])}]
        nil))))
