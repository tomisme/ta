(ns ta.views.planbook
  (:require [ta.views.common :refer [sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(def resource-icon-names {:booklet "book"
                          :worksheet "file"})

(defn lesson-list-item
  [id {:keys [description subject year finished activities]} selected?]
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
               :onClick #(dispatch [:add-lesson])}
          (icon "plus") "New Lesson"]
      (if (not (seq lessons)) [:div {:class "ui active inline loader"}]
        [:div {:class "ui items"}
          (for [lesson lessons
                :let [[id content] lesson
                      selected? (= open-lesson id)]]
            ^{:key (str id)} [lesson-list-item id content selected?])])])))

(defn lesson-inspector
  [id {:keys [year subject finished description title]}]
  [:div {:class "ui form" :style #js {:marginBottom 15}}
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
      [:div {:class "field" :style #js {:marginTop 8}}
        [checkbox #(dispatch [:update-lesson id :finished %])
                  "Ready" finished]]]])

(defn lesson-objectives
  [id {:keys [objectives]}]
  [:div
   [:h4 {:class "ui horizontal divider header"} "Objectives"]
   [:p "Here you will be able to select from unit objectives"]
   [:p]])

(def test-activity
  {:tags [{:text "japan"}
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
  [id activities]
  [:div])

(defn lesson-activities
  [id {:keys [activities]}]
  [:div
    [:h4 {:class "ui horizontal divider header"} "Activities"]
    (let [{:keys [tags description length resources]} test-activity]
      [:div {:class "ui fluid card"}
        [:div {:class "content"}
          [:div {:class "ui blue label"
                 :style #js {:marginRight 10}}
            (str length "m")]
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
              [:div {:class "ui teal label"}
                (icon (type resource-icon-names))
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
                           [:div {:class "ui green label"}
                             text (icon "delete icon")])
                       tags)]])])

(defn lessons-panel
  [lessons open-lesson]
  [:div {:class "row"}
    [:div {:class "five wide column"}
      [lesson-list lessons open-lesson]]
    [:div {:class "eleven wide column"}
      (if @open-lesson
          [:div {:class "ui segment"}
            [lesson-inspector @open-lesson (get @lessons @open-lesson)]
            #_[lesson-objectives]
            [lesson-activities]]
          [:div {:class "circular ui large green inverted label"}
            (icon "left arrow") "Got time to work on a lesson from your stack?"])]])

(defn planbook-view
  []
  (let [planbook-page (subscribe [:planbook-page])
        lessons (subscribe [:lessons])
        open-lesson (subscribe [:open-lesson])
        tabs [{:key :activities :str "Activities"}
              {:key :lessons :str "Lessons"}
              {:key :units :str "Units"}]]
    (fn []
      (let [active-page @planbook-page]
        [:div {:class "ui grid"}
          [:div {:class "centered row"}
           [:div {:class "two wide colum"}
             [:div {:class "ui buttons"}
              (for [tab tabs :let [{:keys [key str]} tab]]
                ^{:key str}
                  [:button {:onClick #(dispatch [:set-planbook-page key])
                            :class (sem (if (= active-page key) "active")
                                        "ui attached button")}
                    str])]]]
          (condp = @planbook-page
            :lessons [lessons-panel lessons open-lesson]
            :units [:p "Lets make some units!"]
            :activities [:p "Let's look at some activities!"])]))))
