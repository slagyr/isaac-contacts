(ns isaac.contacts.contacts
  "Resolve arriving handles against the loaded contact table."
  (:require
    [clojure.string :as str]
    [isaac.foundation.config.loader :as loader]))

(defn- contact-id [id]
  (if (keyword? id) (name id) (str id)))

(defn- email [value]
  (some-> value str/lower-case))

(defn- handles [contact]
  (or (:handles contact) []))

(defn- matching-contact [contacts match?]
  (some (fn [[id contact]]
          (when (some match? (handles contact))
            [id contact]))
        (sort-by (comp contact-id key) contacts)))

(defn identify
  "Name a handle from the loaded snapshot, or nil when no contact owns it."
  [handle]
  (let [contacts (:contacts (loader/snapshot "contact identifier at turn entry"))
        match (or (when (and (:comm handle) (:id handle))
                    (matching-contact contacts #(and (= (keyword (:comm handle)) (:comm %))
                                                     (= (:id handle) (:id %)))))
                  (when-let [address (email (:email handle))]
                    (matching-contact contacts #(= address (email (:email %))))))]
    (when-let [[id contact] match]
      {:kind   :contact
       :id     (contact-id id)
       :name   (:name contact)
       :comm   (:comm handle)
       :handle (:id handle)})))

(defn- handle-key [handle]
  (cond
    (and (:comm handle) (:id handle)) [:comm (:comm handle) (:id handle)]
    (:email handle) [:email (email (:email handle))]))

(defn- invalid-handles [contacts]
  (for [[id contact] contacts
        handle (handles contact)
        :when (not (or (and (= #{:email} (set (keys handle))) (string? (:email handle)))
                       (and (= #{:comm :id} (set (keys handle)))
                            (keyword? (:comm handle)) (string? (:id handle)))))]
    {:key (str "contacts." (contact-id id) ".handles")
     :value (str "contact " (contact-id id) " has invalid handle " (pr-str handle))}))

(defn- duplicate-errors [contacts]
  (second
    (reduce
      (fn [[seen errors] [id handle]]
        (if-let [key (handle-key handle)]
          (if-let [owner (get seen key)]
            (if (= owner id)
              [seen errors]
              [seen (conj errors {:key "contacts"
                                  :value (str "contacts " (contact-id owner) " and " (contact-id id)
                                              " share handle " (pr-str key))})])
            [(assoc seen key id) errors])
          [seen errors]))
      [{} []]
      (for [[id contact] (sort-by (comp contact-id key) contacts)
            handle (handles contact)]
        [id handle]))))

(defn check-config [{:keys [config]}]
  {:errors (vec (concat (invalid-handles (:contacts config))
                        (duplicate-errors (:contacts config))))
   :warnings []})
