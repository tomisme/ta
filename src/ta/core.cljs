(ns ta.core
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  (:require [ta.views]))

(defcard "#ta
  ##Welcome!

  `ta` is a tool for teachers. That makes this a tool for developers of tools
  for teachers!

  Clojurescript source code is separated into areas of user functionality like so:

  ```
  │
  ├── common
  │
  ├── calendar
  ├── classes
  ├── activities
  ├── lessons
  ├── resources
  └── units
  ```
  Clojurescript files in `common` contain reagent components used throughout the app.

  Each other folder contains some combination of:

  - `handlers.cljs` - handles relevant user interaction events
  - `views.cljs` - presentation logic for multi-platform components
  - `subs.cljs` - data retrieval

  A root version of each of these files stitches everything together.
  ")
