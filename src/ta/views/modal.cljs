(ns ta.views.modal
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [shodan.inspection :refer [inspect]]
            [ta.views.common :refer [sem icon-el e->val]]
            [re-frame.core :as r]))

(defn do-and-close [do-me] (do-me) (r/dispatch [:close-modal]))

(defn modal
  [{:keys [active? type] :as arg-map}]
  (let [do-and-close (fn [thing-to-do] (thing-to-do)
                                       (r/dispatch [:close-modal]))]
    [:div {:class (sem "ui modal transition #_active"
                       (if active? "visible"))
           :style {:top "10%"}}
      [:i {:class "close icon"
           :on-click #(r/dispatch [:close-modal])}]
      (case type
        :confirm [:div {:class "header"} "Confirm"]
        [:div {:class "header"} (:header arg-map)])
      (case type
        :scrollbox [:div {:class "content"}
                     [:div {:style {:overflow-y "auto"
                                    :max-height "500px"}} (:content arg-map)]]
        :confirm   [:div {:class "image content"}
                     [:div {:class "image"} (icon-el (:i arg-map))]
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

(defn add-activity-resource-modal
  [{:keys [active? data]}]
  (let [activity-id (:id data)
        resource {:name (:name data)
                  :url (:url data)}
        handle-submit (fn []
                        (r/dispatch [:add-resource-to-activity activity-id resource])
                        (r/dispatch [:close-modal]))]
    [:div {:class (sem "ui modal transition" (if active? "active"))
           :style {:top "10%"}}
      [:i {:class "close icon"
           :on-click #(r/dispatch [:close-modal])}]
    [:div {:class "header"} "Add a new resource"]
    [:div {:class "content"}
      [:div {:class "ui form"}
        [:div {:class "field"}
          [:div {:class "ui fluid left icon input"}
            (icon-el "file text outline")
            [:input {:type "text"
                     :placeholder "Name of the resource"
                     :on-change #(r/dispatch [:update-modal :name (e->val %)])
                     :value (:name data)}]]]
        [:div {:class "field"}
          [:div {:class "ui fluid left icon input"}
            (icon-el "globe")
            [:input {:type "text"
                     :placeholder "http://url-of-resource.com"
                     :on-change #(r/dispatch [:update-modal :url (e->val %)])
                     :value (:url data)}]]]]]
      [:div {:class "actions"}
        [:div {:class "ui green button"
               :on-click handle-submit}
          "Add Resource"]]]))

(defn global-modal
  []
  (let [active? (r/subscribe [:modal :active?])
        data    (r/subscribe [:modal :data])
        type    (r/subscribe [:modal :type])]
    (fn []
      (case @type
        :delete-activity [modal {:active? @active?
                                 :type :confirm
                                 :i "trash"
                                 :question "Are you sure you want to delete this activity?"
                                 :on-yes #(r/dispatch [:activity :delete (:id @data)])}]
        :add-resource-to-activity [add-activity-resource-modal {:active? @active?
                                                                :data @data}]
        nil))))

(defn dimmer
  [{:keys [active?]}]
  [:div {:class (sem "ui dimmer #_modals page #_transition #_visible"
                     (if active? "active"))}])
