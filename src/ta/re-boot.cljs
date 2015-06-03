(ns ta.re-boot)

(defn align [direction el]
  [:div {:class (case direction :left "pull-left"
                                :right "pull-right"
                                :center "text-center")}
    el])

(def panel-styles {:default "panel-default"
                   :primary "panel-primary"
                   :success "panel-success"
                   :info "panel-info"
                   :warning "panel-warning"
                   :danger "panel-danger"
                   nil "panel-default"})

(defn icon ;from Font Awesome - edit close bars
  ([name] (icon name :s))
  ([name size]
    (let [size-str (case size
                         :s nil
                         :m "fa-lg"
                         :l "fa-2x")
          name-str (str "fa-" name)]
    [:i {:class (clojure.string/join " " ["fa" name-str size-str])}])))

(defn panel
  ([content]
    [:div {:class "panel panel-default"}
      [:div {:class "panel-body"} content]])
  ([title content]
    (panel title content :default))
  ([title content style]
    {:pre [(contains? panel-styles style)]}
    [:div {:class (str "panel " (panel-styles style))}
      [:div {:class "panel-heading"}
        [:h3 {:class "panel-title"} title]]
      [:div {:class "panel-body"} content]]))
