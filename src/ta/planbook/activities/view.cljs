(ns ta.planbook.activities.view
  (:require-macros [reagent.ratom :refer [reaction]]
                   [devcards.core :as dc :refer [defcard deftest defcard-rg]])
  (:require [cljs.test :refer-macros [is testing]]
            [reagent.core :as rg]
            [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.util :refer [year-levels subjects]]
            [ta.common.remantic :refer [sem e->val icon-el input-el]]))

(def test-activities
  {:activity-a {:description "I am :activity-a"
                :length 20
                :resources {:list-item-a :resource-a
                            :list-item-b :resource-b}
                :steps {:step-a {:content "I am a step"
                                 :num 1}}}
   :activity-b {:description "I am :activity-b"
                :length 20
                :resources {:list-item-a :resource-a}
                :steps {:step-a {:content "I am a step"
                                 :num 1}}}
   :activity-c {:description "I am :activity-c"
                :length 20
                :resources {:list-item-a :resource-a}
                :steps {:step-a {:content "I am a step"
                                 :num 1}}}})

(def test-resources
  {:resource-a {:name "I am :resource-a, a cool worksheet?"
                :url "http://reddit.com"}
   :resource-b {:name "I am :resource-b, a cool worksheet?"
                :url "http://google.com"}
   :resource-c {:name "I am :resource-c, a cool worksheet?"
                :url "http://digg.com"}})

(def test-tags
  {:tag-a {:label "english"
           :type :subject}
   :tag-b {:label "8s"
           :type :year}
   :tag-c {:label "essay writing"
           :type :content}})

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
               text (icon-el "delete")])]])))

(defn steps-panel
  [{:keys [id]}]
  (let [dyn-sub (reaction (rf/subscribe [:activity-steps @id]))
        steps   (reaction @@dyn-sub)]
    (fn []
      (let [new-step    #(rf/dispatch [:new-activity-step @id])
            delete-step #(rf/dispatch [:delete-activity-step @id %])
            toggle-step #(rf/dispatch [:toggle-activity-step @id %])
            move-step   (fn [key direction]
                          (rf/dispatch [:move-activity-step @id key direction]))
            update-step (fn [key event]
                          (rf/dispatch [:update-activity-step @id key (e->val event)]))]
        [:div
         [:div {:class "ui internally celled grid"}
          (for [[id {:keys [num content open?]}] @steps]
            ^{:key id}
            [:div {:class "row"}
             [:div {:class "right aligned two wide middle aligned column"}
              (if open? [:a {:on-click #(move-step id :up)}
                         (icon-el "up arrow")])
              [:a {:class "ui blue circular label"
                   :on-click #(toggle-step id)}
               num]
              (if open? [:a {:on-click #(move-step id :down)}
                         (icon-el "down arrow")])]
             [:div {:class "fourteen wide column"}
              [:div {:class "ui grid"}
               [:div {:class "row"}
                [:div {:class "fourteen wide column"}
                 [:div {:class "ui transparent fluid input"}
                  [input-el {:type "text"
                             :val content
                             :placeholder "Enter the details of the step"
                             :on-blur #(update-step id %)}]]]
                [:div {:class "two wide middle aligned column"}
                 [:a {:on-click #(delete-step id)}
                  (icon-el "delete")]]]]]])]
         [:center>div {:class "ui blue labeled icon button"
                       :on-click new-step}
          (icon-el "plus") "Add Step"]]))))

(defn resource-tageything
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
          ^{:key k} [resource-tageything k id plan-id])
        [:span (icon-el "left arrow") "Click here to add a new resource link"])]]))

(defcard-rg resources-panel-empty
  [resources-panel])

(defcard-rg resources-panel
  "WORK IN PROGRESS"
  (fn [state _]
    [resources-panel {:resource-ids @state
                      :remove-resource #(swap! state dissoc %)
                      :resources test-resources}])
  (rg/atom (-> test-activities :activity-a :resources))
  {:inspect-data true})

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
         [steps-panel {:id id}]
         [resources-panel {:plan-id @id
                           :resource-ids resources}]
         #_[tag-list {:plan-type :activity
                      :tag-ids tags}]
         [:div {:class "ui center aligned basic segment"}
          [:button {:class "ui green labeled icon button"
                    :on-click #(rf/dispatch [:set-planbook-open :activity nil])}
           "Done" (icon-el "check")]
          [:button {:class "ui red labeled icon button"
                    :on-click #(rf/dispatch
                                [:launch-modal :delete-activity {:id @id}])}
           "Delete" (icon-el "trash")]]]))))

(defn activity-list-item
  [{:keys [id activity selected? select-activity]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class (sem "ui" (if selected? "black") "link card")
           :on-click #(select-activity id)}
     [:div {:class "content"}
      [:span description]]]))

(defn activity-list
  [{:keys [activities selected select-activity]}]
  [:div {:class "ui center aligned basic segment"}
   (if (seq activities)
     (for [[id activity] activities]
       ^{:key (str id)} [activity-list-item {:id id
                                             :activity activity
                                             :selected? (= selected id)
                                             :select-activity select-activity}])
     [:p "No activities.... yet?"])])

(defcard-rg activity-list-empty
  [activity-list])

(defcard-rg activity-list
  (fn [state _]
    [activity-list {:activities test-activities
                    :selected (:selected @state)
                    :select-activity #(swap! state assoc :selected %)}])
  (rg/atom {:selected (key (second test-activities))})
  {:inspect-data true})

(defn activities-tab
  []
  (let [activities (rf/subscribe [:activities])
        selected   (rf/subscribe [:open :activity])]
    [:div {:class "centered row"}
     [:div {:class (sem (if @selected "six" "sixteen") "wide column")}
      [activity-list {:activities @activities
                      :selected @selected
                      :select-activity #(rf/dispatch [:set-planbook-open :activity %])}]]
     (if @selected [activity-editor])]))
