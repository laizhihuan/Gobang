(ns gobang.core
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic])
  (:require [clojure.tools.logging :as logger])
  (:import [java.util List ArrayList]
           [gui Point]))

(def max-x 14)
(def max-y 14)

(defn list-point-to-vec-map
  [chesses-list]
  (let [chesses (vec chesses-list)]
    (map #(bean %) chesses)))

(defn first-point
  [enemy-chesses-vec]
  (let [point (first enemy-chesses-vec)
        x (:x point)
        y (:y point)]
    (if (or (= 0 x) (= 0 y) (= max-x x) (= max-y y))
      {:x (/ max-x 2) :y (/ max-y 2)}
      {:x (- x 1) :y y})))

(defn out-side?
  [point]
  (let [x (:x point)
        y (:y point)]
    (and (and (< 0 x) (< 0 y))
         (and (> x max-x) (> y max-y)))))

(defn coll-contains?
  [chesses point]
  (let [x (:x point)
        y (:y point)]
    (-> (empty? (filter #(and (= x (:x %)) (= y (:y %))) chesses))
        (false?))))

(defn right-next-chess
  "右边的棋子"
  [curr-chess next-index]
  (assoc curr-chess :x (+ (:x curr-chess) next-index)))

(defn left-next-chess
  "左边的棋子"
  [curr-chess next-index]
  (assoc curr-chess :x (- (:x curr-chess) next-index)))

(defn up-next-chess
  "上边的棋子"
  [curr-chess next-index]
  (assoc curr-chess :y (+ (:y curr-chess) next-index)))

(defn down-next-chess
  "下边的棋子"
  [curr-chess next-index]
  (assoc curr-chess :y (- (:y curr-chess) next-index)))

(defn right-up-next-chess
  [curr-chess next-index]
  (-> (right-next-chess curr-chess next-index)
      (up-next-chess next-index)))

(defn right-down-next-chess
  [curr-chess next-index]
  (-> (right-next-chess curr-chess next-index)
      (down-next-chess next-index)))

(defn left-up-next-chess
  [curr-chess next-index]
  (-> (left-next-chess curr-chess next-index)
      (up-next-chess next-index)))

(defn left-down-next-chess
  [curr-chess next-index]
  (-> (left-next-chess curr-chess next-index)
      (down-next-chess next-index)))

(defn valid-chess?
  [my-chesses-vec enemy-chesses-vec point]
  (and (not (nil? point))
       (not (out-side? point))
       (not (coll-contains? my-chesses-vec point))
       (not (coll-contains? enemy-chesses-vec point))))

(defmulti dir-next-chess
  (fn [dir chesses-vec count] (:dir dir)))

(defmethod dir-next-chess "up"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (up-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(up-next-chess last-point %) (range 1 count)))
        (up-next-chess last-point count)))))

(defmethod dir-next-chess "right"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (right-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(right-next-chess last-point %) (range 1 count)))
        (right-next-chess last-point count)))))

(defmethod dir-next-chess "right-up"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (right-up-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(right-up-next-chess last-point %) (range 1 count)))
        (right-up-next-chess last-point count)))))

(defmethod dir-next-chess "right-down"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (right-down-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(right-down-next-chess last-point %) (range 1 count)))
        (right-down-next-chess last-point count)))))

(defmethod dir-next-chess "down"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (down-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(down-next-chess last-point %) (range 1 count)))
        (down-next-chess last-point count)))))

(defmethod dir-next-chess "left"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (left-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(left-next-chess last-point %) (range 1 count)))
        (left-next-chess last-point count)))))

(defmethod dir-next-chess "left-up"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (left-up-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(left-up-next-chess last-point %) (range 1 count)))
        (left-up-next-chess last-point count)))))

(defmethod dir-next-chess "left-down"
  [dir chesses-vec count]
  (let [last-point (last chesses-vec)]
    (if (= 1 count)
      (left-down-next-chess last-point 1)
      (when (every? #(coll-contains? chesses-vec %) (map #(left-down-next-chess last-point %) (range 1 count)))
        (left-down-next-chess last-point count)))))

(defn find-chesses
  [chesses-vec count]
  (conj []
    (dir-next-chess {:dir "up"} chesses-vec count)
    (dir-next-chess {:dir "right"} chesses-vec count)
    (dir-next-chess {:dir "down"} chesses-vec count)
    (dir-next-chess {:dir "left"} chesses-vec count)
    (dir-next-chess {:dir "right-up"} chesses-vec count)
    (dir-next-chess {:dir "left-up"} chesses-vec count)
    (dir-next-chess {:dir "right-down"} chesses-vec count)
    (dir-next-chess {:dir "left-down"} chesses-vec count)))


(defn do-analysis-chesses
  "分析自己的棋型"
  [my-chesses-vec enemy-chesses-vec]
  (let [alive-4-chess (filter #(valid-chess? my-chesses-vec enemy-chesses-vec %) (find-chesses my-chesses-vec 4))
        alive-3-chess (filter #(valid-chess? my-chesses-vec enemy-chesses-vec %) (find-chesses my-chesses-vec 3))
        alive-2-chess (filter #(valid-chess? my-chesses-vec enemy-chesses-vec %) (find-chesses my-chesses-vec 2))
        alive-next-chess (filter #(valid-chess? my-chesses-vec enemy-chesses-vec %) (find-chesses my-chesses-vec 1))
        _ (logger/info (str "do-analysis 4 " alive-4-chess " 3 " alive-3-chess " 2 " alive-2-chess " 1 " alive-next-chess))]
    (cond
      (not (nil? (seq alive-4-chess)))
      alive-4-chess
      (not (nil? (seq alive-3-chess)))
      alive-3-chess
      (not (nil? (seq alive-2-chess)))
      alive-2-chess
      (not (nil? (seq alive-next-chess)))
      alive-next-chess)))

;;定义棋局回合
(defrel round r1 chess-fn)

(fact round "first-round" {:x 7 :y 7})

(defn logic-analysis
  [my-chesses-vec enemy-chesses-vec]
  (run* [q]
    (== q (first (do-analysis-chesses my-chesses-vec enemy-chesses-vec)))))

(defn do-analysis
  "AI对当前局势进行分析，并作出下棋决定"
  [my-chesses enemy-chesses all-free-chess-point]
  (let [my-chesses-vec (list-point-to-vec-map my-chesses)
        enemy-chesses-vec (list-point-to-vec-map enemy-chesses)
        all-free-chess-point-vec (list-point-to-vec-map all-free-chess-point)
        enemy-chesses-count (count enemy-chesses-vec)]
    (if (= 1 enemy-chesses-count)
      (let [first (first-point enemy-chesses-vec)
            _ (logger/info (str "first" first))]
        (Point. (:x first) (:y first)))
      (let [chesses (logic-analysis my-chesses-vec enemy-chesses-vec)
            next-chess (first chesses)] 
          (Point. (:x next-chess) (:y next-chess))))))




