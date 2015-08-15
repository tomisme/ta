(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [adzerk/boot-cljs      "0.0-3308-0"       :scope "test"]
                 [adzerk/boot-cljs-repl "0.1.10-SNAPSHOT"  :scope "test"]
                 [adzerk/boot-reload    "0.3.1"            :scope "test"]
                 [pandeiro/boot-http    "0.6.3-SNAPSHOT"   :scope "test"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.1"]
                 [shodan "0.4.2"]
                 [com.andrewmcveigh/cljs-time "0.3.10"]
                 [cljsjs/moment "2.9.0-0"]
                 [secretary "1.2.3"]
                 [bidi "1.20.0"]
                 [matchbox "0.0.7"]])

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
  (task-options! cljs {:optimizations :advanced})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :unified-mode true
                       :source-map true}
                 reload {:on-jsload 'ta.core/init})
  identity)

(deftask dev
  []
  (comp (development)
        (run)))

(deftask prod
  []
  (comp (production)
        (build)))
