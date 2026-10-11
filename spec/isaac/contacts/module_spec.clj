(ns isaac.contacts.module-spec
  (:require
    [speclj.core :refer :all]
    [isaac.foundation.module.protocol :as module]
    [isaac.contacts.module :as contacts-module]))

(describe "isaac.contacts.module"
  (it "create-module returns a valid Module instance"
    (should (module/module? (contacts-module/create-module)))))
