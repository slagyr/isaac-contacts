(ns isaac.contacts.contacts-spec
  (:require
    [isaac.contacts.contacts :as sut]
    [isaac.foundation.config.loader :as loader]
    [speclj.core :refer :all]))

(def contacts
  {:cordelia {:name "Cordelia Vane" :title "Harbormaster" :weight 3
              :handles [{:comm :logbook :id "cordelia-7"} {:email "cordelia@marigold.example"}]}
   :hieronymus {:name "Hieronymus Finch" :handles [{:email "hieronymus@marigold.example"}]}
   :bartholomew {:name "Bartholomew Quill"}})

(describe "contacts"
  (context "identifying handles"
    (it "names a matching comm and id before a different contact's email"
      (with-redefs [loader/snapshot (fn [_] {:contacts contacts})]
        (should= {:kind :contact :id "cordelia" :name "Cordelia Vane" :comm :logbook :handle "cordelia-7"}
                 (sut/identify {:kind :handle :comm :logbook :id "cordelia-7"
                                :email "hieronymus@marigold.example"}))))
    (it "matches an email regardless of case while keeping the arriving handle"
      (with-redefs [loader/snapshot (fn [_] {:contacts contacts})]
        (should= {:kind :contact :id "cordelia" :name "Cordelia Vane" :comm :semaphore :handle "flag-22"}
                 (sut/identify {:kind :handle :comm :semaphore :id "flag-22"
                                :email "CORDELIA@MARIGOLD.EXAMPLE"}))))
    (it "leaves a handle not listed by any contact alone"
      (with-redefs [loader/snapshot (fn [_] {:contacts contacts})]
        (should-be-nil (sut/identify {:kind :handle :comm :logbook :id "stranger-1"}))))
    (it "does not match a contact with no handles"
      (with-redefs [loader/snapshot (fn [_] {:contacts contacts})]
        (should-be-nil (sut/identify {:kind :handle :comm :semaphore :id "bartholomew"})))))
  (context "config validation"
    (it "names both contacts and the repeated comm and id"
      (let [cfg {:contacts {:cordelia (:cordelia contacts)
                            :bartholomew {:name "Bartholomew Quill" :handles [{:comm :logbook :id "cordelia-7"}]}}}
            result (sut/check-config {:config cfg})]
        (should= "contacts" (get-in result [:errors 0 :key]))
        (should-contain "cordelia" (get-in result [:errors 0 :value]))
        (should-contain "bartholomew" (get-in result [:errors 0 :value]))
        (should-contain "cordelia-7" (get-in result [:errors 0 :value]))))
    (it "rejects a handle that has neither an email nor a comm and id"
      (let [result (sut/check-config {:config {:contacts {:cordelia {:name "Cordelia Vane" :handles [{}]}}}})]
        (should= "contacts.cordelia.handles" (get-in result [:errors 0 :key]))))
    (it "rejects a handle mixing email with comm and id"
      (let [result (sut/check-config {:config {:contacts {:cordelia {:name "Cordelia Vane"
                                                              :handles [{:email "cordelia@marigold.example"
                                                                         :comm :logbook :id "cordelia-7"}]}}}})]
        (should= "contacts.cordelia.handles" (get-in result [:errors 0 :key]))))
    (it "names both contacts and a repeated email regardless of case"
      (let [cfg {:contacts {:cordelia (:cordelia contacts)
                            :bartholomew {:name "Bartholomew Quill" :handles [{:email "CORDELIA@MARIGOLD.EXAMPLE"}]}}}
            result (sut/check-config {:config cfg})]
        (should= "contacts" (get-in result [:errors 0 :key]))
        (should-contain "cordelia" (get-in result [:errors 0 :value]))
        (should-contain "bartholomew" (get-in result [:errors 0 :value]))
        (should-contain "cordelia@marigold.example" (get-in result [:errors 0 :value])))))
  )
