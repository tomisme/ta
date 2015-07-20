(ns ta.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [shodan.inspection :refer [inspect]]
            [clojure.string :as string]))

(def weekdays [:mon :tues :wed :thurs :fri])

(def day-strings {:mon   "Monday"
                  :tues  "Tuesday"
                  :wed   "Wednesday"
                  :thurs "Thursday"
                  :fri   "Friday"
                  :sat   "Saturday"
                  :sun   "Sunday"})

(defn sem [& parts]
  "Returns a space separated string for use as an HTML component's :class"
  (string/join " " parts))

(defn e->val [e]
  "Take an input component's :on-change event and returns the its value"
  (-> e .-target .-value))

(defn ibut [value]
  "Handy inspection button! Click to inspect a value in the console"
  [:button {:on-click #(inspect value)} "what?"])

(defn icon
  "Takes name string and optional size keyword and returns an icon element"
  ([name] (icon name :m))
  ([name size]
    (let [size-str (case size
                         :s "small"
                         :m ""
                         :l "large")]
    [:i {:class (sem "icon" name size-str)}])))

(defn flag-img [country]
  "Takes a country keyword and returns a flag icon element"
  (case country
    :australia [:i {:class "australia flag" :style #js {:paddingLeft 5}}]))

(defn test-dropdown []
  [:div {:class "ui selection dropdown"}
    [:input {:type "hidden" :name "gender"}]
    [:div {:class "default text"} "Gender"]
    [:i {:class "dropdown icon"}]
    [:div {:class "menu"}
     [:div {:class "item" :data-value "1"} "Male"]
     [:div {:class "item" :data-value "0"} "Female"]]])

(def page-links [{:key :timetable
                  :icon "calendar"
                  :label "Timetable"
                  :url "#/timetable"}
                 {:key :planner
                  :icon "book"
                  :label "Planbook"
                  :url "#/planner"}
                 {:key :classes
                  :icon "table"
                  :label "Classes"
                  :url "#/classes"}])

(defn nav-links [current-page]
  (let [active-page @current-page]
    (map (fn [link]
           (let [class (sem (if (= (:key link) active-page) "active") "item")
                 icon [:span {:style #js {:paddingRight 4}} (icon (:icon link))]
                 label (:label link)
                 url (:url link)]
             ^{:key label} [:a {:class class :href url} icon label]))
         page-links)))

(defn top-bar [active-page]
  (let [user (subscribe [:user])
        name (reaction (:name @user))
        flag (reaction (:flag @user))]
    (fn []
        [:div {:class "row"}
          [:div {:class "column"}
            [:div {:class "ui secondary pointing menu"}
              (nav-links active-page)
              [:div {:class "right menu"}
                [:a {:class "ui item"}
                  @name (flag-img @flag) (icon "caret down")]]]]])))

(defn class-slot [period lesson]
  (if (= :dot period)
    [:div {:class "ui card"}
      [:div {:class "center aligned content"} (icon "coffee")]]
    [:div {:class "ui green card"}
      [:div {:class "content"}
        [:div {:class "ui label ribbon green"
               :style #js {:marginBottom 10}} period]
        [:h4  {:class "ui sub header"} (:title lesson)]
        [:div {:class "description"} (:text lesson)]]]))

;; TODO: loop through new timetable data
(defn weekday [day]
  (let [lessons   (subscribe [:lessons day])
        timetable (subscribe [:timetable day])]
    (fn []
      [:div
        [:center (day-strings day)]
        (map (fn [a b c] ^{:key c} [class-slot a b]) @timetable @lessons [1 2 3])])))

(defn week-view []
  (let [week (subscribe [:active-week])]
    [:div {:class "ui centered grid"}
      [:div {:class "row"}
        [:div {:class "center aligned column"}
          [:a {:href (str "#/timetable/week/" (dec @week))}
            (icon "chevron circle left")]
          (str "Week " @week)
          [:a {:href (str "#/timetable/week/" (inc @week))}
            (icon "chevron circle right")]]]
      [:div {:class "row"}
        (map #(with-meta
               (vector :div {:class "five wide column"} [weekday %])
               {:key %})
          weekdays)]]))

(defn timetable-panel []
  (let []
    (fn []
      [week-view])))

(defn planner-panel []
  (let []
    (fn []
      [:p "Don't worry, you'll be able to plan lessons pretty soon. I can feel it."])))

(defn mini-schedule [schedule color]
  (inspect schedule)
  [:div {:class "ui equal width center aligned padded grid"}
    (for [day [:mon :tues :wed :thurs :fri]]
      ^{:key day} [:div {:class "row"}
       (for [cell (day schedule)]
         [:div {:class (str (if (= :selected cell) (name color)) " column")}
          (icon "circle" :s)])]
    )])

;; first param is the internal id for the class, e.g. :-JufNb6aylkTrW5irdQu
(defn class-card [[id {:keys [name color schedule] :as class}]]
  ^{:key id} [:div {:class (sem "ui card" (clojure.core/name color))}
    [:div {:class "content"}
      name]
    [:div {:class "content"}
      (mini-schedule schedule color)]])

(defn color-selector [selected-color on-selected]
  [:div {:class "ui mini horizontal divided list"}
          (doall (for [color [:red :orange :yellow :green :blue :pink]
                       :let [color-str (name color)
                             selected? (= selected-color color)]]
                   ^{:key color-str} [:div {:class "item"}
                     (if selected?
                       [:b color-str]
                       [:a {:on-click #(on-selected color)}
                         color-str])]))])

(defn schedule-selector [on-change schedule new-schedule selected-color]
  "When an empty class slot in the table is clicked, it calls on-change
   with an updated schedule"
  (let [day-labels {:mon   "Mo"
                    :tues  "Tu"
                    :wed   "We"
                    :thurs "Th"
                    :fri   "Fr"}]
  [:div
    [:span "When do you have the class?"]
    [:div {:class "ui equal width center aligned padded grid"}
      (for [row [:label 0 1 2 3 4]]
       ^{:key row} [:div {:class "row"}
         (for [col [:label :mon :tues :wed :thurs :fri]]
           ;; what kind if table cell are we?
           (let [col-label? (= col :label)
                 row-label? (= row :label)
                 taken?     (not= :dot (get-in schedule [col row]))
                 slot?      (not (or col-label? row-label? taken?))
                 selected?  (if slot?
                              (= (get-in new-schedule [col row]) :selected))
                 on-select  (if slot?
                              #(on-change (assoc-in new-schedule
                                                    [col row] (if selected?
                                                                :slot
                                                                :selected))))
                 color-str  (cond row-label? ""
                                  col-label? ""
                                  taken? "black"
                                  selected? (name selected-color))]
             ^{:key col} [:div {:class (sem color-str "column")
                                :on-click on-select}
             (cond
               row-label? [:span (col day-labels)]
               col-label? [:span (str "S" (inc row))]
               taken? (icon "close")
               selected? (icon "check")
               :else (icon "circle" :s))]))])]]))

(defn new-class-form [schedule]
  (let [schedule           (subscribe [:schedule])
        new-class          (subscribe [:new-class])
        new-class-name     (reaction (:name     @new-class))
        new-class-color    (reaction (:color    @new-class))
        new-class-schedule (reaction (:schedule @new-class))]
    (fn []
      [:div {:class "ui card"}
        [:div {:class "center aligned content"}
          [:div {:class "header"} "Create a new class"]
          [:div {:class "meta"} "It will be added to your timetable"]]
        [:div {:class "center aligned content"}
          [:div {:class "ui fluid input"}
          [:input {:type "text"
                   :value @new-class-name
                   :placeholder "Choose a name for the class"
                   :on-change #(dispatch [:update-new-class :name (e->val %)])}]]
          (color-selector @new-class-color
                          #(dispatch [:update-new-class :color %]))
          (schedule-selector #_(dispatch [:inspect %])
                             #(dispatch [:update-new-class :schedule %])
                             @schedule
                             @new-class-schedule
                             @new-class-color)]
        [:div {:class "center aligned extra content"}
          [:button {:class "ui labeled icon button"
                    :on-click #_(dispatch [:inspect @new-class]) #(dispatch [:add-new-class])}
            (icon "plus")
            "Add Class"]]])))

(defn classes-panel []
  (let [classes (subscribe [:classes])]
    (fn []
      [:div {:class "ui centered grid"}
        [:div {:class "row"}
          [:div {:class "eight wide column"}
            [:div (map class-card @classes)]]
          [:div {:class "eight wide column"}
            [new-class-form]]]])))

(defn main-panel [active-page]
  (let []
    (fn []
      [:div {:class "row"}
        [:div {:class "column"}
          (case @active-page :timetable [timetable-panel]
                             :planner [planner-panel]
                             :classes [classes-panel]
                             [:span "No Panel Found?"])]])))

(defn app []
  (let [active-page (subscribe [:active-page])]
    (fn []
      [:div {:class "ui grid container" :style #js {:margin 10}}
        [top-bar active-page]
        [main-panel active-page]])))
