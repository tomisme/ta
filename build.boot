(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [adzerk/boot-cljs      "0.0-3308-0"       :scope "test"]
                 [adzerk/boot-cljs-repl "0.1.10-SNAPSHOT"  :scope "test"]
                 [adzerk/boot-reload    "0.3.1"            :scope "test"]
                 [pandeiro/boot-http    "0.6.3-SNAPSHOT"   :scope "test"]
                 [clj-time "0.9.0"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.1"]
                 [shodan "0.4.2"]
                 [secretary "1.2.3"]
                 [matchbox "0.0.6"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask build []
  (comp (speak)
        (cljs)))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced
                       ;; pseudo-names true is currently required
                       ;; https://github.com/martinklepsch/pseudo-names-error
                       ;; hopefully fixed soon
                       :pseudo-names true})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :unified-mode true
                       :source-map true}
                 reload {:on-jsload 'ta.core/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))
