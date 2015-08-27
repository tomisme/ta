(defproject ta "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3297"]
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
                 [matchbox "0.0.7"]
                 [json-html "0.3.4"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.5"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "ta.core/on-js-reload" }

              :compiler {:main ta.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/ta.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/ta.js"
                         :main ta.core
                         :optimizations :advanced
                         :pretty-print false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
