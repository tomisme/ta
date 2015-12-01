(ns ta.views
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]])
  (:require [ta.classes.views]))

(defcard "#Views
  Wherever possible, view/presentation logic should *static* or 'dumb', so it can be used for
  different purposes/platforms.

  *Idea:* functionality namespaces should only contain static reagent code, containers used to
  stitch things together could go here?")
