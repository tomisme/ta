(ns ta.db)

(def default-db
  { :username "Tom Hutchinson"
    :view :week
    :timetable {:mon  [:dot "8 Media" "11 General English"]
                :tues ["11 General English" :dot :dot]
                :wed  ["8 Media" "10 Modified English" :dot]}
    :lessons {:mon  [{}
                     {:title "Film #2"
                      :text "MOAR MOVIES"}
                     {:title "Autobiographies #4"
                      :text "Well, an autobiography movie!"}]
              :tues [{:title "Autobiographies #5"
                      :text "Maybe we'll watch some movies"}
                      {}
                      {}]
              :wed  [{:title "Film #3"
                      :text "MOAAAAAR MOVIES"}
                     {:title "Visual Texts #1"
                      :text "Da MOVIES"}
                      {}]}})
