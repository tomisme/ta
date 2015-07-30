(ns ta.views.planbook
  (:require [ta.views.common :refer [ibut sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(defn lesson-list-item
  [id {:keys [description subject year finished activities]} selected?]
  (let [desc-str (if (not= description "") description  ;; TODO: better validation
                                           "Untitled")]
  [:div {:class (sem "ui" (if selected? "black") "link card")}
    [:div {:class "content"
           :onClick #(dispatch [:set-open-lesson id])}
      [:div {:style #js {:marginBottom 4}}
        (if year [:div {:class "ui olive mini label"} (str "Year " year)])
        (if subject [:div {:class "ui blue mini label"} subject])
        (if finished [:div {:class "ui green mini label"} "Ready"])]
      [:div desc-str]]]))

(defn lesson-list [lessons-atom open-lesson-atom]
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

(defn lesson-inspector [id {:keys [year subject finished description title] :as lesson}]
  [:div {:class "ui segment"}
    [:div {:class "ui form"}
      [:div {:class "field"}
        [:input {:type "text"
                 :placeholder "Enter a short description for your lesson"
                 :value description
                 :onChange #(dispatch [:update-lesson id :description (e->val %)])}]]
      [:div {:class "fields"}
        [:div {:class "field"}
          [dropdown {:on-change #(dispatch [:update-lesson id :year (e->val %)])
                     :value year
                     :options year-levels
                     :starting "Year"}]]
        [:div {:class "field"}
          [dropdown {:on-change #(dispatch [:update-lesson id :subject (e->val %)])
                     :value subject
                     :options subjects
                     :starting "Subject"}]]
        [:div {:class "field" :style #js {:marginTop 8}}
          [checkbox #(dispatch [:update-lesson id :finished %])
                    "Ready" finished]]]]])

(defn planbook-panel []
  (let [lessons (subscribe [:lessons])
        open-lesson (subscribe [:open-lesson])]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "five wide column"}
          [lesson-list lessons open-lesson]]
        [:div {:class "eleven wide column"}
          (if @open-lesson [lesson-inspector @open-lesson (get @lessons @open-lesson)]
                           [:div {:class "ui green compact inverted segment"}
                             (icon "left arrow") "Got time to work on a lesson from your stack?"])]])))
