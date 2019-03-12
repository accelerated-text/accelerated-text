(ns lt.tokenmill.nlg.api.lexicon
  (:require [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource])
  (:gen-class
    :name lt.tokenmill.nlg.api.LexiconHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :lexicon))

(defn get-key-id [key]
  (-> (re-find #"^.+\.(\d+)$" key)
      (second)
      (Integer/parseInt)))

(defn get-used-key-ids [m]
  (set (map #(get-key-id (get % :key)) m)))

(defn next-keys [db word]
  (let [matches (ops/scan! db {:attr-conds {:word [:eq word]}})
        ids (-> (get-used-key-ids matches) (conj 0))]
    (map (partial format (str word "\.%s"))
         (remove #(contains? ids %) (range)))))

(defn sort-entries [coll]
  (into [] (->> coll
                (sort-by (fn [{:keys [word key]}]
                           [word (get-key-id key)])))))

(defn create-single [db key {:keys [word] :as request-body}]
  (utils/do-update (when (< 0 (count word)) (partial ops/write! db key)) (dissoc request-body :key)))

(defn create-multiple [db request-body]
  (let [request-map (group-by #(get % :word) request-body)
        words (keys request-map)
        key-map (zipmap words (map (partial next-keys db) words))]
    (flatten (->> words
                  (pmap (fn [word]
                          (mapv (partial create-single db)
                                (get key-map word)
                                (get request-map word))))))))

(defn create [request-body]
  (let [db (get-db)]
    (if (map? request-body)
      (create-single db (first (next-keys db (get request-body :word))) request-body)
      (utils/add-status (create-multiple db request-body)))))

(defn update-single [db request-body]
  (utils/do-update (partial ops/update! db)
                   (get request-body :key)
                   (dissoc request-body :key)))

(defn update-multiple [db request-body]
  (map (partial update-single db) request-body))

(defn update [path-params request-body]
  (let [db (get-db)]
    (if (map? request-body)
      (update-single db request-body)
      (utils/add-status (update-multiple db request-body)))))

(defn process-search-response [resp offset limit]
  (let [offset (max 0 (Integer/parseInt (or offset "0")))
        limit (max 0 (Integer/parseInt (or limit "15")))
        count (count resp)]
    {:offset     offset
     :totalCount count
     :items      (-> resp
                     (sort-entries)
                     (subvec (min count offset)
                             (min count (+ offset limit))))}))

(defn search [{:keys [query offset limit] :as query-params} path-params]
  (let [db (get-db)
        length (count query)]
    (utils/do-return
      (comp (fn [resp]
              (when (seq resp) (process-search-response resp offset limit)))
            (partial ops/scan! db))
      {:attr-conds {:word (cond
                            (= \* (first query) (last query)) [:contains (subs query 1 (dec length))]
                            (= \* (first query)) [:contains (subs query 1 length)]
                            (= \* (last query)) [:begins-with (subs query 0 (dec length))]
                            :else [:eq query])}})))

(def -handleRequest
  (resource/build-resource {:put-handler  update
                            :post-handler create
                            :get-handler  search}
                           true))
