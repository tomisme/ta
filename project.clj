(defproject ta "0.1.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [reagent "0.5.1"]
                 [re-frame "0.4.1"]
                 [matchbox "0.0.8-SNAPSHOT"]
                 [secretary "1.2.3"]
                 [shodan "0.4.2"]
                 [devcards "0.2.0-8"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.5"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :cljsbuild {
              :builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel {:devcards true}
                        :compiler {:main "ta.core"
                                   :asset-path "js/compiled/devcards_out"
                                   :output-to  "resources/public/js/compiled/ta_devcards.js"
                                   :output-dir "resources/public/js/compiled/devcards_out"
                                   :optimizations :none}}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel {:on-jsload "ta.core/render-app"}
                        :compiler {:main "ta.core"
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/ta.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :none}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/ta.js"
                                   :main "ta.core"
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
