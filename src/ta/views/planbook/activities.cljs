(ns ta.views.planbook.activities
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.common :refer [sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

#_(defn tag-list
  [{:keys [plan-type tag-ids]}]
  {:pre [(or (= :lesson plan-type) (= :activity plan-type))]}
  (let [all-tags  (subscribe [:tags])]
    (fn []
      [:div {:class "ui fluid segment"}
        [:div {:class "content"}
          [:button {:class "ui icon disabled button"
                    :style {:marginRight 10}}
            [:i {:class "large icons"}
              (icon "tag")
              (icon "corner plus")]]
          (inspect tag-ids)
          #_(for [tag-id tag-ids]
            ^{:key (str id)}
              [:div {:class "ui yellow label"}
                text (icon "delete icon")])]])))

(defn resource-thing
  [k resource-id plan-id]
  (let [resource-data (subscribe [:resource resource-id])]
    (fn []
      [:div {:class "ui violet label"}
        [:a {:href (:url @resource-data)}
          (icon "globe") (:name @resource-data)]
        [:a {:on-click #(dispatch [:remove-resource-from-activity k plan-id])}
          (icon "delete icon")]])))

(defn resources-panel
  [{:keys [resource-ids plan-id]}]
  [:div {:class "ui fluid segment"}
    [:div {:class "content"}
      [:button {:class "ui icon button"
                :style {:marginRight 10}
                :on-click #(dispatch [:modal :launch :add-resource-to-activity {:id plan-id}])}
        [:i {:class "large icons"}
          (icon "file text")
          (icon "corner plus")]]
      (for [[k id] resource-ids]
        ^{:key k} [resource-thing k id plan-id])]])

(defn activity-editor
  []
  (let [id       (subscribe [:open :activity])
        dyn-sub  (reaction (subscribe [:activity @id]))
        activity (reaction @@dyn-sub)]
    (fn []
      (let [{:keys [description length resources tags]} @activity
            update-attr (fn [attr]
                          #(dispatch [:activity :update @id attr (e->val %)]))]
        [:div {:class "ten wide column"}
          [:div {:class "ui fluid segment"}
            [:div {:class "ui transparent fluid input"}
              [:input {:type "text"
                       :value description
                       :placeholder "Enter a short description of the activity"
                       :on-change (update-attr :description)}]]]
          [resources-panel {:plan-id @id
                           :resource-ids resources}]
          #_[tag-list {:plan-type :activity
                     :tag-ids tags}]
          [:div {:class "ui center aligned segment"}
            [:button {:class "ui green labeled icon button"
                      :on-click #(dispatch [:set-open :activity nil])}
              "Done" (icon "check")]
            [:button {:class "ui red labeled icon button"
                      :on-click #(dispatch [:modal :launch :delete-activity {:id @id}])}
              "Delete" (icon "trash")]]]))))

(defn activity-list-item
  [{:keys [id activity selected?]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class (sem "ui" (if selected? "black") "link card")
           :onClick #(dispatch [:set-open :activity id])}
      [:div {:class "content"}
        [:span description]]]))

(defn activity-list
  []
  (let [activities (subscribe [:activities])
        selected (subscribe [:open :activity])]
    (fn []
      [:div {:class "ui center aligned segment"}
        [:button {:class "ui labeled icon button"
                  :on-click #(dispatch [:activity :new])}
         (icon "plus") "New Activity"]
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
  (let [selected (subscribe [:open :activity])]
    [:div {:class "centered row"}
      [:div {:class (sem (if @selected "six" "sixteen") "wide column")}
        [activity-list]]
      (if @selected [activity-editor])]))
