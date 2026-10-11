Feature: Contacts config — one handle belongs to one contact
  Two contacts that list the same handle would make the party behind it
  ambiguous, so the config is refused and the error names both contacts.

  Tracked by isaac-o7tm.

  Scenario: two contacts listing the same handle is a config error naming both
    Given config file "contacts/cordelia.edn" containing:
      """
      {:name "Cordelia Vane" :handles [{:comm :logbook :id "cordelia-7"}]}
      """
    And config file "contacts/bartholomew.edn" containing:
      """
      {:name "Bartholomew Quill" :handles [{:comm :logbook :id "cordelia-7"}]}
      """
    When the config is loaded
    Then the config has validation errors matching:
      | key      | value                                                |
      | contacts | #"(?s)(?=.*bartholomew)(?=.*cordelia).*cordelia-7.*" |
