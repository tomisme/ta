(ns ta.core
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  (:require [ta.views]))

(defcard "#ta
  ##Welcome!

  This is the developer playground and testing environment for the `ta` application.

  `ta` is a tool for teachers. That makes this a tool for developers of tools
  for teachers!

  ##Project Structure
  Clojurescript source code is separated into areas of user functionality like so:

  ```
  ├── common
  ├── calendar
  ├── classes
  └── planbook
      ├── activities
      ├── lessons
      ├── resources
      └── units
  ```
  Each folder usually contains some combination of:

  - `handlers.cljs` - domain specific logic
  - `views.cljs` - presentation (for all platforms, as shared as possible)
  - `subs.cljs` - data retrieval

  A root version of each of these files stitches everything together.")
