(defproject ta "0.1.0-SNAPSHOT"
  :description "Teachers, be Zen"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [clj-time "0.9.0"]
                 [reagent "0.5.0-alpha3"]
                 [re-frame "0.4.1"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.1"]]

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

  :figwheel { :css-dirs ["resources/public/css"]})
