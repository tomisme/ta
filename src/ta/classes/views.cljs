(ns ta.classes.views
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]])
  (:require [re-frame.core :as rf]
            [ta.common.util :as util]
            [ta.common.components :refer [sem e->val icon-el]]))

(defcard test
  "I am a test!")
