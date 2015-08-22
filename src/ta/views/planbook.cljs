;; TODO work on activities tab, create activities independently of lessons

(ns ta.views.planbook
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.common :refer [sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

(defn lesson-details
  [{:keys [id lesson]}]
  (let [{:keys [year subject finished description title]} @lesson
        update-fn (fn [attribute]
                    #(dispatch [:lesson :update @id attribute (e->val %)]))]
    [:div {:class "ui form"
           :style {:marginBottom 15}}
      [:div {:class "right aligned fields"}
        [:div {:class "field"}
          [:button {:class "ui green icon button"
                    :on-click #(dispatch [:set-open :lesson nil])}
              (icon "check")]]
        [:div {:class "field"}
          [:button {:class "ui red icon button"
                    :on-click #(dispatch [:lesson :delete @id])}
            (icon "trash")]]]
      [:div {:class "field"}
        [:input {:onChange (update-fn :description)
                 :type "text"
                 :placeholder "Enter a short description for your lesson"
                 :value description}]]
      [:div {:class "fields"}
        [:div {:class "field"}
          [dropdown {:on-change (update-fn :year)
                     :value year
                     :options year-levels
                     :starting "Year"}]]
        [:div {:class "field"}
          [dropdown {:on-change (update-fn :subject)
                     :value subject
                     :options subjects
                     :starting "Subject"}]]
       [:div {:class "field"}
         [:button {:class "ui labeled icon disabled button"}
           (icon "calendar")
           "Teach"]]
       [:div {:class "field" :style {:marginTop 5}}
          [checkbox #(dispatch [:lesson :update @id :finished %])
                    "Ready" finished]]]]))

(defn lesson-activity-card
  [{:keys [activity]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class "ui fluid card"}
      [:div {:class "content"} description]
      [:div {:class "content"}
        [:button {:class "ui icon disabled button"
                  :style {:marginRight 10}}
          [:i {:class "large icons"}
            (icon "file text")
            (icon "corner plus")]]
        (for [resource resources
              :let [{:keys [description url type sides]} resource]]
          ^{:key description}
            [:div {:class "ui violet label"}
              [:a (icon (type resource-icon-names))]
              description
              (icon "delete icon")])]
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
                  (icon "chevron circle up" :l)]
                [:div {:class "row"}
                  [:div {:class "ui blue label"}
                    (str length "m")]]
                [:a {:class "row"}
                  (icon "chevron circle down" :l)]]
              [:div {:class "fourteen wide column"}
                [lesson-activity-card {:activity activity}]]]))])

(defn lesson-activities
  [{:keys [id]}]
  (let [activities (reaction @(subscribe [:lesson-activities @id]))]
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
              (icon "cube") "Add an Activity"]]]]]))

(defn lesson-details-panel
  [{:keys [id]}]
  (let [lesson (reaction @(subscribe [:lesson @id]))]
    (fn []
      [:div {:class "ui segment"}
        [lesson-details {:id id :lesson lesson}]
        [lesson-activities {:id id}]])))

(defn lesson-list-item
  [{:keys [id lesson selected?]}]
  (let [{:keys [description subject year finished activity-ids]} lesson]
    [:div {:class (sem "ui" (if selected? "black") "link card")}
      [:div {:class "content"
             :on-click #(dispatch [:set-open :lesson id])}
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
                  (icon i) str])])

(defn lesson-list
  []
  (let [lessons  (subscribe [:lessons :filtered])
        selected (subscribe [:open :lesson])
        filters  (subscribe [:filter :lessons])]
    (fn []
      [:div {:class "five wide column"}
        [:div {:class "ui labeled icon button"
               :on-click #(dispatch [:lesson :new])}
          (icon "plus") "New"]
        (menu {:class "ui fluid vertical menu"
               :active-id (if (contains? @filters :finished)
                            (:finished @filters))
               :items [{:id  false
                        :str "Unfinished"
                        :i   "circle outline"
                        :on  #(dispatch [:set-filter :lessons :finished false])}
                       {:id  true
                        :str "Finished"
                        :i   "check circle outline"
                        :on  #(dispatch [:set-filter :lessons :finished true])}]})
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

(defn activity-list-item
  [{:keys [id activity]}]
  (let [{:keys [description length resources tags]} activity]
    [:div {:class "ui link card"}
      [:div {:class "content"}
        [:span description]]]))

(defn activity-list
  []
  (let [activities (subscribe [:activities])]
    (fn []
      [:div {:class "ui center aligned segment"}
        [:button {:class "ui labeled icon button"
                  :on-click #(dispatch [:activity :new])}
         (icon "plus") "Create New Activity"]
        (if (seq @activities)
          (for [[id activity] @activities]
            ^{:key (str id)} [activity-list-item {:id id :activity activity}])
          [:p "No lessons.... yet?"])])))

(defn activity-editor
  [{:keys [id activity]}]
  (let [id       (subscribe [:open :activity])
        activity (reaction @(subscribe [:activity @id]))]
    (fn []
      (let [{:keys [description length resources tags]} activity]
        [:div {:class "ui fluid segment"}
          [:div {:class "content"} description]
          [:div {:class "content"}
            [:button {:class "ui icon disabled button"
                      :style {:marginRight 10}}
              [:i {:class "large icons"}
                (icon "file text")
                (icon "corner plus")]]
            (for [resource resources
                  :let [{:keys [description url type sides]} resource]]
              ^{:key description}
                [:div {:class "ui violet label"}
                  [:a (icon (type resource-icon-names))]
                  description
                  (icon "delete icon")])]
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
                         tags)]]))))

(defn activities-tab
  []
  [:div {:class "centered row"}
    [:div {:class "six wide column"}
      [activity-list]]
    [:div {:class "ten wide column"}
      [activity-editor]]])

(defn lessons-tab
  []
  (let [open-lesson (subscribe [:open :lesson])]
    (fn []
      [:div {:class "row"}
        [lesson-list]
        [:div {:class "eleven wide column"}
          (if @open-lesson
            [lesson-details-panel {:id open-lesson}])]])))

(defn planbook-view
  []
  (let [active-tab (subscribe [:open :tab])
        tabs [{:key :activities :str "Activities" :i "cubes"}
              {:key :lessons    :str "Lessons"    :i "file outline"}
              {:key :units      :str "Units"      :i "briefcase"}]]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "centered one colum row"}
          [:div {:class "colum"}
            (doall
              (for [tab tabs :let [{:keys [key str i]} tab]]
                ^{:key str}
                  [:button {:on-click #(dispatch [:set-open :tab key])
                            :class (sem (if (= @active-tab key) "active")
                                        "ui labeled icon button")}
                    (icon i) str]))]]
       (condp = @active-tab
          :units      [:p "Lets make some units!"]
          :lessons    [lessons-tab]
          :activities [activities-tab])])))
