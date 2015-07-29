(ns ta.views.planbook
  (:require [ta.views.common :refer [ibut sem e->val icon checkbox dropdown]]
            [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]))

(def year-levels [7 8 9 10 11 12])

(def subjects ["English" "Media"])

(defn lesson-item [id {:keys [description subject year activities]}]
  (let [desc-str (if (not= description "") description  ;; TODO: better validation
                                           "Untitled")]
  [:div {:class "item"}
    [:div {:class "content"}
      [:a {:class "header"
           :onClick #(dispatch [:set-open-lesson id])} desc-str]]]))

(defn lesson-list [lessons]
  (if (seq lessons)
    [:div {:class "ui divided items"}
      (for [lesson lessons
            :let [[id content] lesson]]
        ^{:key (:description content)} [lesson-item id content])]
    [:span "Loading Lessons..."]))

(defn lesson-detail [id {:keys [year subject finished description title] :as lesson}]
  [:div {:class "ui segment"}
    [:div {:class "ui form"}
      [:div {:class "fields"}
        [:div {:class "field"}
          [dropdown {:value year
                     :options year-levels
                     :starting "Year"}]]
        [:div {:class "field"}
          [dropdown {:value subject
                     :options subjects
                     :starting "Subject"}]]
        [:div {:class "field" :style #js {:marginTop 8}}
          [checkbox #(dispatch [:update-lesson id :finished %])
                    "Ready" finished]]]
      [:input {:type "text"
               :placeholder "Enter a one line description for your lesson"
               :value description
               :onChange #(dispatch [:update-lesson id :description (e->val %)])}]]])

(defn planbook-panel []
  (let [lessons (subscribe [:lessons])
        open-lesson (subscribe [:open-lesson])]
    (fn []
      [:div {:class "ui grid"}
        [:div {:class "four wide column"}
          [lesson-list @lessons]]
        [:div {:class "twelve wide column"}
          (if @open-lesson [lesson-detail @open-lesson (get @lessons @open-lesson)]
                           [:span "Select a lesson to work on"])]])))
