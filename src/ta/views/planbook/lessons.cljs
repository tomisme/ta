(ns ta.views.planbook.lessons
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [shodan.inspection :refer [inspect]]
            [ta.util :refer [year-levels subjects]]
            [ta.views.common :refer [sem e->val icon-el checkbox-el dropdown-el]]))


(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

(defn lesson-details
  [{:keys [id lesson]}]
  (let [{:keys [year subject finished description title]} @lesson
        update-fn (fn [attribute]
                    #(rf/dispatch [:lesson :update @id attribute (e->val %)]))
        delete-modal {:type :confirm
                      :i "trash"
                      :question "Are you sure you want to delete this lesson?"
                      :on-yes #(rf/dispatch [:lesson :delete @id])}]
    [:div {:class "ui form"
           :style {:marginBottom 15}}
      [:div {:class "right aligned fields"}
        [:div {:class "field"}
          [:button {:class "ui green icon button"
                    :on-click #(rf/dispatch [:set-planbook-open :lesson nil])}
            (icon-el "check")]]
        [:div {:class "field"}
          [:button {:class "ui red icon button"
                    :on-click #(rf/dispatch [:modal :update delete-modal])}
            (icon-el "trash")]]]
      [:div {:class "field"}
        [:input {:onChange (update-fn :description)
                 :type "text"
                 :placeholder "Enter a short description for your lesson"
                 :value description}]]
      [:div {:class "fields"}
        [:div {:class "field"}
          [dropdown-el {:on-change (update-fn :year)
                        :value year
                        :options year-levels
                        :starting "Year"}]]
        [:div {:class "field"}
          [dropdown-el {:on-change (update-fn :subject)
                        :value subject
                        :options subjects
                        :starting "Subject"}]]
       [:div {:class "field"}
         [:button {:class "ui labeled icon disabled button"}
           (icon-el "calendar")
           "Teach"]]
       [:div {:class "field" :style {:marginTop 5}}
          [checkbox-el {:label "Ready"
                        :checked finished
                        :on-change #(rf/dispatch [:lesson :update @id :finished %])}]]]]))

(defn lesson-activity-card
  [{:keys [activity]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class "ui fluid card"}
      [:div {:class "content"} description]
      [:div {:class "content"}
        [:button {:class "ui icon disabled button"
                  :style {:marginRight 10}}
          [:i {:class "large icons"}
            (icon-el "file text")
            (icon-el "corner plus")]]
        (for [resource resources
              :let [{:keys [description url type sides]} resource]]
          ^{:key description}
            [:div {:class "ui violet label"}
              [:a (icon-el (type resource-icon-names))]
              description
              (icon-el "delete")])]
      [:div {:class "content"}
        [:button {:class "ui icon disabled button"
                  :style {:marginRight 10}}
          [:i {:class "large icons"}
            (icon-el "tag")
            (icon-el "corner plus")]]
        (map-indexed (fn [i {:keys [text]}]
                       ^{:key (str i text)}
                         [:div {:class "ui yellow label"}
                           text (icon-el "delete")])
                     tags)]]))

(defn lessson-activity-card-list
  [{:keys [lesson-id activities]}]
    [:div {:class "ui grid"}
      (doall
        (for [[id activity] @activities
              :let [length (get activity :length)]]
          ^{:key (str id)}
            [:div {:class "row"}
              [:div {:class "center aligned two wide column"
                     :style {:paddingRight 0}}
                [:a {:class "row"}
                  (icon-el {:name "chevron circle up"
                            :size :large})]
                [:div {:class "row"}
                  [:div {:class "ui blue label"}
                    (str length "m")]]
                [:a {:class "row"}
                  (icon-el {:name "chevron circle down"
                            :size :large})]]
              [:div {:class "fourteen wide column"}
                [lesson-activity-card {:activity activity}]]]))])

(defn lesson-activities
  [{:keys [id]}]
  (let [activities (reaction @(rf/subscribe [:lesson-activities @id]))]
    [:div
      [:h4 {:class "ui horizontal divider header"} "Activities"]
      [:div {:class "ui grid"}
        [:div {:class "one column row"}
          [:div {:class "column"}
            [lessson-activity-card-list {:lesson-id id
                                         :activities activities}]]]
        [:div {:class "center aligned one column row"
               :style {:paddingTop 0}}
          [:div {:class "column"}
            [:button {:class "ui labeled icon disabled button"}
              (icon-el "cube") "Add an Activity"]]]]]))

(defn lesson-details-panel
  [{:keys [id]}]
  (let [lesson (reaction @(rf/subscribe [:lesson @id]))]
    (fn []
      [:div {:class "ui segment"}
        [lesson-details {:id id :lesson lesson}]
        [lesson-activities {:id id}]])))

(defn lesson-list-item
  [{:keys [id lesson selected?]}]
  (let [{:keys [description subject year finished activity-ids]} lesson]
    [:div {:class (sem "ui" (if selected? "black") "link card")}
      [:div {:class "content"
             :on-click #(rf/dispatch [:set-planbook-open :lesson id])}
        [:div {:style {:marginBottom 7}}
          (if year [:div {:class "ui olive mini label"} (str "Year " year)])
          (if subject [:div {:class "ui blue mini label"} subject])
          (if finished [:div {:class "ui green mini label"} "Ready"])]
        [:div (if (= description "")
                "Untitled"
                description)]]]))

(defn menu-item
  [{:keys [id content active? handler]}]
  ^{:key id} [:a {:class (sem (if active? "active") "item")
                  :on-click handler}
               content])

(defn menu
  "Each item in items sequence should be a map with at least :id and :content"
  [{:keys [class items active-id]}]
  [:div {:class class}
    (for [{:keys [id str on i]} items]
      ^{:key id} [:a {:class (sem (if (= id active-id) "active") "item")
                      :on-click on}
                  (icon-el i) str])])

(defn lesson-list
  []
  (let [lessons  (rf/subscribe [:lessons :filtered])
        selected (rf/subscribe [:open :lesson])
        filters  (rf/subscribe [:filter :lessons])]
    (fn []
      [:div {:class "five wide column"}
        [:div {:class "ui labeled icon button"
               :on-click #(rf/dispatch [:lesson :new])}
          (icon-el "plus") "New"]
        (menu {:class "ui fluid vertical menu"
               :active-id (if (contains? @filters :finished)
                            (:finished @filters))
               :items [{:id  false
                        :str "Unfinished"
                        :i   "circle outline"
                        :on  #(rf/dispatch [:set-planbook-filter :lessons :finished false])}
                       {:id  true
                        :str "Finished"
                        :i   "check circle outline"
                        :on  #(rf/dispatch [:set-planbook-filter :lessons :finished true])}]})
        [:div {:class "ui center aligned basic segment"}
          (if (not (seq @lessons))
            [:div {:class "ui active inline loader"}]
            [:div {:class "ui items"}
              (doall
                (for [lesson @lessons
                      :let [[id content] lesson
                            selected? (= @selected id)]]
                  ^{:key (str id)}
                    [lesson-list-item {:id id
                                       :lesson content
                                       :selected? selected?}]))])]])))

(defn lessons-tab
  []
  (let [open-lesson (rf/subscribe [:open :lesson])]
    (fn []
      [:div {:class "row"}
        [lesson-list]
        [:div {:class "eleven wide column"}
          (if @open-lesson
            [lesson-details-panel {:id open-lesson}])]])))
