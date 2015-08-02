(ns ta.views.planbook
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [ta.views.common :refer [sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

(defn lesson-list-item
  [id {:keys [description subject year finished activity-ids]} selected?]
  (let [desc-str (if (= description "") "Untitled" description)]
    [:div {:class (sem "ui" (if selected? "black") "link card")}
      [:div {:class "content"
             :onClick #(dispatch [:set-open-lesson id])}
        [:div {:style #js {:marginBottom 4}}
          (if year [:div {:class "ui olive mini label"} (str "Year " year)])
          (if subject [:div {:class "ui blue mini label"} subject])
          (if finished [:div {:class "ui green mini label"} "Ready"])]
        [:div desc-str]]]))

(defn lesson-list
  [lessons-atom open-lesson-atom]
  (fn []
    (let [lessons @lessons-atom
          open-lesson @open-lesson-atom]
      [:div {:class "ui center aligned stacked segment"}
        [:div {:class "ui header"} "Lesson Stack"]
        [:div {:class "ui labeled icon button"
               :onClick #(dispatch [:new-empty-lesson])}
          (icon "plus") "New Lesson"]
      (if (not (seq lessons)) [:div {:class "ui active inline loader"}]
        [:div {:class "ui items"}
          (for [lesson lessons
                :let [[id content] lesson
                      selected? (= open-lesson id)]]
            ^{:key (str id)} [lesson-list-item id content selected?])])])))

(defn lesson-details
  [id {:keys [year subject finished description title]}]
  [:div {:class "ui form" :style #js {:marginBottom 15}}
    [:div {:class "right aligned fields"}
      [:div {:class "field"}
        [:button {:class "ui green icon button"
                  :onClick #(dispatch [:set-open-lesson nil])}
            (icon "check")]]
      [:div {:class "field"}
        [:button {:class "ui red icon button"
                  :onClick #(dispatch [:remove-lesson id])}
          (icon "trash")]]]
    [:div {:class "field"}
      [:input {:type "text"
               :placeholder "Enter a short description for your lesson"
               :value description
               :onChange #(dispatch [:update-lesson
                                     id :description (e->val %)])}]]
    [:div {:class "fields"}
      [:div {:class "field"}
        [dropdown {:on-change #(dispatch [:update-lesson
                                          id :year (e->val %)])
                   :value year
                   :options year-levels
                   :starting "Year"}]]
      [:div {:class "field"}
        [dropdown {:on-change #(dispatch [:update-lesson
                                          id :subject (e->val %)])
                   :value subject
                   :options subjects
                   :starting "Subject"}]]
      [:div {:class "field" :style #js {:marginTop 5}}
        [checkbox #(dispatch [:update-lesson id :finished %])
                  "Ready" finished]]
      [:div {:class "field"}
        [:button {:class "ui compact small labeled icon button"}
         (icon "calendar")
         "Choose Slot"]]]])

(defn lesson-objectives
  [id {:keys [objectives]}]
  [:div
   [:h4 {:class "ui horizontal divider header"} "Objectives"]
   [:p "To add lesson objectives, you need to "
       [:a {:class "ui  horizontal label"
            :style #js {:marginLeft 3 :marginTop 3}}
         "associate this lesson with a unit"]]
   [:p]])

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
                :url "readwritethink.org/files/resources/printouts/30697_haiku.pdf"}]})

(defn activity-card
  [[id {:keys [description length resources tags]}]]
  [:div {:class "ui fluid card"}
    [:div {:class "content"}
      [:a
        [:div {:class "ui blue label"
               :style #js {:marginRight 10}}
          (str length "m")]]
      description]
    [:div {:class "content"}
      [:button {:class "ui icon button"
                :style #js {:marginRight 10}}
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
      [:button {:class "ui icon button"
                :style #js {:marginRight 10}}
        [:i {:class "large icons"}
          (icon "tag")
          (icon "corner plus")]]
      (map-indexed (fn [i {:keys [text]}]
                     ^{:key (str i text)}
                       [:div {:class "ui yellow label"}
                         text (icon "delete icon")])
                   tags)]])

(defn activity-card-list
  [activities]
  [:div
    (map-indexed (fn [i [id activity]]
                   ^{:key (str id)} [activity-card [id activity]])
                 activities)])

(defn lesson-activities
  [a-id a-lesson]
  (let [all-activities (subscribe [:activities])]
    (fn []
      (let [id @a-id
            {:keys [activity-ids]} @a-lesson]
      [:div
        [:h4 {:class "ui horizontal divider header"}
          "Activities"]
        [:button {:class "ui labeled icon button"}
          (icon "plus") "Create New Activity"]
        (if (seq activity-ids)
          (let [activities @all-activities]
            [activity-card-list
              (for [activity-id activity-ids]
                [activity-id (get activities activity-id)])]))]))))

(defn lesson-details-panel
  []
  (let [lessons (subscribe [:lessons])
        id      (subscribe [:open-lesson])
        lesson  (reaction (get @lessons @id))]
    (fn []
      [:div {:class "ui segment"}
        [lesson-details @id @lesson]
        [lesson-objectives]
        [lesson-activities id lesson]])))

(defn lessons-tab
  [lessons open-lesson]
  [:div {:class "row"}
    [:div {:class "five wide column"}
      [lesson-list lessons open-lesson]]
    [:div {:class "eleven wide column"}
      (if @open-lesson
        [lesson-details-panel @open-lesson (get @lessons @open-lesson)]
        [:div {:class "circular ui large green inverted label"}
          (icon "left arrow") "Got time to work on a lesson from your stack?"])]])

(defn activities-tab
  [activities]
  [:div
    [:button {:class "ui icon button"
              :onClick #(dispatch [:new-activity test-activity])}
      (icon "plus")]
    [activity-card-list @activities]])

(defn planbook-view
  []
  (let [open-page   (subscribe [:planbook-page])
        open-lesson (subscribe [:open-lesson])
        lessons     (subscribe [:lessons])
        activities  (subscribe [:activities])
        tabs [{:key :activities :str "Activities"}
              {:key :lessons    :str "Lessons"}
              {:key :units      :str "Units"}]]
    (fn []
      (let [page @open-page]
        [:div {:class "ui grid"}
          [:div {:class "centered row"}
           [:div {:class "two wide colum"}
             [:div {:class "ui buttons"}
              (for [tab tabs :let [{:keys [key str]} tab]]
                ^{:key str}
                  [:button {:onClick #(dispatch [:set-planbook-page key])
                            :class (sem (if (= page key) "active")
                                        "ui attached button")}
                    str])]]]
          (condp = page
            :units      [:p "Lets make some units!"]
            :lessons    [lessons-tab lessons open-lesson]
            :activities [activities-tab activities])]))))
