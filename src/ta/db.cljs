(ns ta.db)

(def default-db
  { :active-page :timetable

    :user {:name "Tom Hutchinson"
           :flag :australia}

    :classes [{:name "8 Media"
               :schedule [:mon 1 :wed 0]
               :description "Strong class, most students are eager to learn"
               :color :green}
              {:name "11 General English"
               :schedule [:mon 2 :tues 0]
               :description "Low ability, many students moving to TAFE"
               :color :blue}
              {:name "10 Modified English"
               :schedule [:wed 1]
               :description "Several violent students, close supervision req"
               :color :red}]

    :timetable {:mon  [:dot "8 Media" "11 General English"]
                :tues ["11 General English" :dot :dot]
                :wed  ["8 Media" "10 Modified English" :dot]}

    :lessons {:mon  [{}
                     {:title "Film"
                      :text "Editing horror movie practise footage"}
                     {:title "Autobiographies"
                      :text "Well, an autobiography movie!"}]
              :tues [{:title "Autobiographies"
                      :text "Maybe we'll watch some movies"}
                      {}
                      {}]
              :wed  [{:title "Film"
                      :text "MOAAAAAR MOVIES"}
                     {:title "Visual Texts"
                      :text "Da MOVIES"}
                      {}]}})
