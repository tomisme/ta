(ns ta.views.planbook.activities
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.util :refer [year-levels subjects]]
            [ta.views.common :refer [sem e->val icon-el checkbox-el dropdown-el input-el]]))

#_(defn tag-list
  [{:keys [plan-type tag-ids]}]
  {:pre [(or (= :lesson plan-type) (= :activity plan-type))]}
  (let [all-tags  (rf/subscribe [:tags])]
    (fn []
      [:div {:class "ui fluid segment"}
        [:div {:class "content"}
          [:button {:class "ui icon disabled button"
                    :style {:marginRight 10}}
            [:i {:class "large icons"}
              (icon-el "tag")
              (icon-el "corner plus")]]
          (inspect tag-ids)
          #_(for [tag-id tag-ids]
            ^{:key (str id)}
              [:div {:class "ui yellow label"}
                text (icon-el "delete icon")])]])))

(defn steps-panel
  [{:keys [activity-id]}]
  (let [dyn-sub (reaction (rf/subscribe [:activity-steps @activity-id]))
        steps   (reaction @@dyn-sub)]
    (fn []
      (let [delete-step #(rf/dispatch [:delete-activity-step @activity-id %])
            new-step    #(rf/dispatch [:new-activity-step @activity-id])]
        [:div
          [:div {:class "ui internally celled grid"}
            (for [[k {:keys [num content]}] @steps]
              ^{:key (str k)}
                [:div {:class "row"}
                  [:div {:class "right aligned two wide middle aligned column"}
                    [:a {:class "ui blue circular label"} num]]
                  [:div {:class "fourteen wide column"}
                    [:div {:class "ui grid"}
                      [:div {:class "row"}
                        [:div {:class "fourteen wide column"} content]
                        [:div {:class "two wide middle aligned column"}
                          [:a {:on-click #(delete-step k)} (icon-el "delete")]]]]]])]
            [:center>div {:class "ui blue labeled icon button"
                          :on-click new-step}
              (icon-el "plus") "Add Step"]]))))

(defn resource-thing
  [k resource-id plan-id]
  (let [resource-data (rf/subscribe [:resource resource-id])]
    (fn []
      [:div {:class "ui violet label"}
        [:a {:href (:url @resource-data)}
          (icon-el "globe") (:name @resource-data)]
        [:a {:on-click #(rf/dispatch [:remove-resource-from-activity plan-id k])}
          (icon-el "delete")]])))

(defn resources-panel
  [{:keys [resource-ids plan-id]}]
  (let [on-add #(rf/dispatch [:launch-modal :add-resource-to-activity {:id plan-id}])]
    [:div {:class "ui fluid segment"}
      [:div {:class "content"}
        [:button {:class "ui icon button"
                  :style {:marginRight 10}
                  :on-click on-add}
          [:i {:class "large icons"}
            (icon-el "file text")
            (icon-el "corner plus")]]
        (if resource-ids
          (for [[k id] resource-ids]
            ^{:key k} [resource-thing k id plan-id])
          [:span (icon-el "left arrow") "Click here to add a new resource link"])]]))

(defn activity-editor
  []
  (let [id       (rf/subscribe [:open :activity])
        dyn-sub  (reaction (rf/subscribe [:activity @id]))
        activity (reaction @@dyn-sub)]
    (fn []
      (let [{:keys [description length resources]} @activity
            update-attr (fn [attr]
                          #(rf/dispatch [:activity :update @id attr (e->val %)]))]
        [:div {:class "ten wide column"}
          [:div {:class "ui fluid segment"}
            [:div {:class "ui transparent fluid input"}
              [input-el {:type "text"
                         :val description
                         :placeholder "Enter a short description of the activity"
                         :on-blur (update-attr :description)}]]]
          [steps-panel {:activity-id id}]
          [resources-panel {:plan-id @id
                           :resource-ids resources}]
          #_[tag-list {:plan-type :activity
                     :tag-ids tags}]
          [:div {:class "ui center aligned basic segment"}
            [:button {:class "ui green labeled icon button"
                      :on-click #(rf/dispatch [:set-planbook-open :activity nil])}
              "Done" (icon-el "check")]
            [:button {:class "ui red labeled icon button"
                      :on-click #(rf/dispatch [:launch-modal :delete-activity {:id @id}])}
              "Delete" (icon-el "trash")]]]))))

(defn activity-list-item
  [{:keys [id activity selected?]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class (sem "ui" (if selected? "black") "link card")
           :onClick #(rf/dispatch [:set-planbook-open :activity id])}
      [:div {:class "content"}
        [:span description]]]))

(defn activity-list
  []
  (let [activities (rf/subscribe [:activities])
        selected (rf/subscribe [:open :activity])]
    (fn []
      [:div {:class "ui center aligned segment"}
        [:button {:class "ui labeled icon button"
                  :on-click #(rf/dispatch [:activity :new])}
          (icon-el "plus") "New Activity"]
        (if (seq @activities)
          (doall
            (for [[id activity] @activities]
              ^{:key (str id)}
                [activity-list-item {:id id
                                     :activity activity
                                     :selected? (= @selected id)}]))
          [:p "No activities.... yet?"])])))

(defn activities-tab
  []
  (let [selected (rf/subscribe [:open :activity])]
    [:div {:class "centered row"}
      [:div {:class (sem (if @selected "six" "sixteen") "wide column")}
        [activity-list]]
      (if @selected [activity-editor])]))
