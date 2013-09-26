(ns gobang.game-logic
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic])
  (:require [clojure.tools.logging :as logger]))


;;; a biblical family database
(defrel father p1 p2)

;; father(terach,abrahm).
(fact father 'Terach 'Abraham)
(fact father 'Terach 'Nachor)
(fact father 'Terach 'Haran)
(fact father 'Abraham 'Issac)
(fact father 'Haran 'Lot)
(fact father 'Haran 'Milcah)
(fact father 'Haran 'Yiscah)

(run* [x]
      (father 'Abraham 'Issac))
