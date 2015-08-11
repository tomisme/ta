;;  refactor complicated components to pass a map of params
;;  work on activities tab, create activities independently of lessons

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
  (let [{:keys [year subject finished description title]} @lesson]
    [:div {:class "ui form" :style {:marginBottom 15}}
      [:div {:class "right aligned fields"}
        [:div {:class "field"}
          [:button {:class "ui green icon button"
                    :on-click #(dispatch [:set-open-lesson nil])}
              (icon "check")]]
        [:div {:class "field"}
          [:button {:class "ui red icon button"
                    :on-click #(dispatch [:remove-lesson @id])}
            (icon "trash")]]]
      [:div {:class "field"}
        [:input {:type "text"
                 :placeholder "Enter a short description for your lesson"
                 :value description
                 :onChange #(dispatch [:update-lesson @id :description (e->val %)])}]]
      [:div {:class "fields"}
        [:div {:class "field"}
          [dropdown {:on-change #(dispatch [:update-lesson @id :year (e->val %)])
                     :value year
                     :options year-levels
                     :starting "Year"}]]
        [:div {:class "field"}
          [dropdown {:on-change #(dispatch [:update-lesson @id :subject (e->val %)])
                     :value subject
                     :options subjects
                     :starting "Subject"}]]
       [:div {:class "field"}
         [:button {:class "ui labeled icon disabled button"}
           (icon "calendar")
           "Teach"]]
       [:div {:class "field" :style {:marginTop 5}}
          [checkbox #(dispatch [:update-lesson @id :finished %])
                    "Ready" finished]]]]))

(def test-url "readwritethink.org/files/resources/printouts/30697_haiku.pdf")

(def test-activity
  {:tags [{:text "english"}
          {:text "poetry"}
          {:text "8s"}
          {:text "9s"}]
   :description "Students write several Haikus using a starter sheet"
   :length 30 ;in minutes
   :resources [{:sides 2
                :type :worksheet
                :format :pdf
                :description "Haiku Starter"
                :url test-url}]})

(defn activity-card
  [[id {:keys [description length resources tags]}]]
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
                   tags)]])

(defn activity-card-list
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
                [activity-card [id activity]]]]))])

(defn lesson-activities
  [{:keys [id]}]
  (let [activities (reaction @(subscribe [:lesson-activities @id]))]
    [:div
      [:h4 {:class "ui horizontal divider header"} "Activities"]
      [:div {:class "ui grid"}
        [:div {:class "one column row"}
          [:div {:class "column"} [activity-card-list {:lesson-id id
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
             :on-click #(dispatch [:set-open-lesson id])}
        [:div {:style {:marginBottom 7}}
          (if year [:div {:class "ui olive mini label"} (str "Year " year)])
          (if subject [:div {:class "ui blue mini label"} subject])
          (if finished [:div {:class "ui green mini label"} "Ready"])]
        [:div (if (= description "")
                "Untitled"
                description)]]]))

(defn lesson-list
  [{:keys [lessons selected]}]
  [:div {:class "ui center aligned stacked segment"}
    [:div {:class "ui labeled icon button"
           :on-click #(dispatch [:new-empty-lesson])}
      (icon "plus") "Lesson"]
    (if (not (seq @lessons)) [:div {:class "ui active inline loader"}]
      [:div {:class "ui items"}
        (doall
          (for [lesson @lessons
                :let [[id content] lesson
                      selected? (= @selected id)]]
            ^{:key (str id)}
              [lesson-list-item {:id id
                                 :lesson content
                                 :selected? selected?}]))])])

(defn lessons-tab
  []
  (let [lessons     (subscribe [:lessons])
        open-lesson (subscribe [:open-lesson])]
    (fn []
      [:div {:class "row"}
        [:div {:class "five wide column"}
          [lesson-list {:lessons lessons :selected open-lesson}]]
        [:div {:class "eleven wide column"}
          (if @open-lesson
            [lesson-details-panel {:id open-lesson}]
            [:div {:class "circular ui large green inverted label"}
              (icon "left arrow")
              "Got time to work on a lesson from your stack?"])]])))

(defn activities-tab
  [activities]
  [:div {:class "centered row"}
    [:button {:class "ui icon button"
              :on-click #(dispatch [:new-activity test-activity])}
      (icon "plus")]
    #_[activity-card-list @activities]])

(defn planbook-view
  []
  (let [
        activities (subscribe [:activities])
        active-tab (subscribe [:planbook-page])
        tabs [{:key :activities :str "Activities" :i "cube"}
              {:key :lessons    :str "Lessons"    :i "file outline"}
              {:key :units      :str "Units"      :i "briefcase"}]]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "centered one colum row"}
         [:div {:class "colum"}
           (doall (for [tab tabs :let [{:keys [key str i]} tab]]
                    ^{:key str}
                      [:button {:on-click #(dispatch [:set-planbook-page key])
                                :class (sem (if (= @active-tab key) "active")
                                            "ui labeled icon button")}
                   (icon i) str]))]]
        (condp = @active-tab
          :units      [:p "Lets make some units!"]
          :lessons    [lessons-tab]
          :activities [activities-tab activities])])))
