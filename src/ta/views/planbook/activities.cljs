(ns ta.views.planbook.activities
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.common :refer [sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

(defn resource
  [{:keys [description url type sides]}]
  ^{:key description}
    [:div {:class "ui violet label"}
      [:a (icon (type resource-icon-names))]
      description
      (icon "delete icon")])

(defn activity-editor
  []
  (let [id       (subscribe [:open :activity])
        dyn-sub  (reaction (subscribe [:activity @id]))
        activity (reaction @@dyn-sub)]
    (fn []
      (let [{:keys [description length resources tags]} @activity
            update-attr (fn [attr]
                        #(dispatch [:activity :update @id attr (e->val %)]))
            delete-modal {:type :confirm
                          :i "trash"
                          :question "Are you sure you want to delete this activity?"
                          :on-yes #(dispatch [:activity :delete @id])}]
        [:div {:class "ten wide column"}
          [:div {:class "ui fluid segment"}
            [:div {:class "ui transparent fluid input"}
              [:input {:type "text"
                       :value description
                       :placeholder "Enter a short description of the activity"
                       :on-change (update-attr :description)}]]]
          [:div {:class "ui fluid segment"}
            [:div {:class "content"}
              [:button {:class "ui icon disabled button"
                        :style {:marginRight 10}}
                [:i {:class "large icons"}
                  (icon "file text")
                  (icon "corner plus")]]
              (map resource resources)]]
          [:div {:class "ui fluid segment"}
            [:div {:class "content"}
              [:button {:class "ui icon disabled button"
                        :style {:marginRight 10}}
                [:i {:class "large icons"}
                  (icon "tag")
                  (icon "corner plus")]]
              (map-indexed (fn [i {:keys [text]}]
                             ^{:key (str i text)}
                               [:div {:class "ui yellow label"}
                                 text (icon "delete icon")])
                           tags)]]
          [:div {:class "ui center aligned segment"}
            [:button {:class "ui green labeled icon button"
                      :on-click #(dispatch [:set-open :activity nil])}
              "Done" (icon "check")]
            [:button {:class "ui red labeled icon button"
                      :on-click #(dispatch [:modal :launch delete-modal])}
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
         (icon "plus") "Create New Activity"]
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
      [:div {:class "six wide column"}
        [activity-list]]
      (if @selected [activity-editor])]))
