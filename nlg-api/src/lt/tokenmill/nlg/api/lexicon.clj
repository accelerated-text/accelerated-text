(ns lt.tokenmill.nlg.api.lexicon
  (:require [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource])
  (:gen-class
    :name lt.tokenmill.nlg.api.LexiconHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :lexicon))

(defn get-key-ids [coll]
  (map #(Integer/parseInt (second (re-find #"^.+_(\d+)$" (get % :key)))) coll))

(defn next-keys [db word]
  (let [matches (ops/scan! db {:attr-conds {:word [:eq word]}})
        ids (conj (set (get-key-ids matches)) 0)]
    (map (partial format (str word "_%s"))
         (remove #(contains? ids %) (range)))))

(defn insert [db key request-body]
  (utils/do-insert (partial ops/write! db) key (dissoc request-body :key)))

(defn insert-multiple [db keys request-vec]
  (mapv (partial insert db) keys request-vec))

(defn create-multiple [db request-body]
  (let [request-map (group-by #(get % :word) request-body)
        words (keys request-map)
        keys (zipmap words (map (partial next-keys db) words))]
    (into [] (flatten (->> words
                           (pmap (fn [word]
                                   (insert-multiple db
                                                    (get keys word)
                                                    (get request-map word)))))))))

(defn create [request-body]
  (let [db (get-db)]
    (if (map? request-body)
      (insert db (first (next-keys db (get request-body :word))) request-body)
      (create-multiple db request-body))))

(defn update [path-params request-body]
  (let [db (get-db)
        key (get request-body :key)
        body (dissoc request-body :key)]
    (utils/do-update (partial ops/update! db) key body)))

(defn process-search-response [resp offset limit]
  (let [offset (max 0 (Integer/parseInt (or offset "0")))
        limit (max 0 (Integer/parseInt (or limit "15")))
        count (count resp)]
    {:offset     offset
     :totalCount count
     :items      (subvec resp (min count offset) (min count (+ offset limit)))}))

(defn search [{:keys [query offset limit] :as query-params} path-params]
  (let [db (get-db)
        length (count query)]
    (utils/do-return
      (comp (fn [resp]
              (when (seq resp) (process-search-response resp offset limit)))
            (partial ops/scan! db))
      {:attr-conds {:word (cond (= \* (first query) (last query)) [:contains (subs query 1 (dec length))]
                                #_(= \* (first query) [:contains (subs query 1 length)])
                                (= \* (last query) [:begins-with (subs query 0 (dec length))])
                                :else [:eq query])}})))

(def -handleRequest
  (resource/build-resource {:put-handler  update
                            :post-handler create
                            :get-handler  search}
                           true))
