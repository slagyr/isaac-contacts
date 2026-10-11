Feature: Contacts — a handle becomes the contact it belongs to
  A contact is a party known by name. Each contact is one file under
  config/contacts/, named for its id:

    {:name    "Cordelia Vane"
     :title   "Harbormaster"
     :weight  3
     :handles [{:comm :logbook :id "cordelia-7"}
               {:email "cordelia@marigold.example"}]}

  The module is an identifier on isaac-agent's :isaac.agent/identifiers
  berth. Agent hands it each handle in a turn's :from and :for. A handle
  one contact lists is stored as that contact:

    {:kind :contact :id "cordelia" :name "Cordelia Vane"
     :comm :logbook :handle "cordelia-7" :authenticated true}

  :comm and :handle keep how the party arrived. A handle is matched on its
  comm and id first, then on its email, so the same person is the same
  contact on every comm. A handle no contact lists is left as it arrived.
  Naming a party never vouches for it: the comm's :authenticated claim is
  stored as the comm made it. :title and :weight are the contact's own
  data; they are not copied onto the turn.

  Tracked by isaac-o7tm.

  Background:
    Given default Grover setup
    And the isaac EDN file "config/contacts/cordelia.edn" exists with:
      | path    | value                                                                    |
      | name    | Cordelia Vane                                                            |
      | title   | Harbormaster                                                             |
      | weight  | 3                                                                        |
      | handles | [{:comm :logbook :id "cordelia-7"} {:email "cordelia@marigold.example"}] |
    And the following sessions exist:
      | name   |
      | harbor |

  @wip
  Scenario: a handle a contact lists by comm and id is stored as that contact
    When a turn is submitted with:
      | key                | value        |
      | id                 | tide-9       |
      | input              | Leave harbor |
      | session            | harbor       |
      | from.kind          | handle       |
      | from.comm          | logbook      |
      | from.id            | cordelia-7   |
      | from.authenticated | true         |
    And isaac is run with "turns show tide-9"
    Then the stdout matches:
      | pattern                  |
      | from.kind: contact       |
      | (?m)^from\.id: cordelia$ |
      | from.name: Cordelia Vane |
      | from.comm: logbook       |
      | from.handle: cordelia-7  |
      | from.authenticated: true |
      | for.kind: contact        |
      | (?m)^for\.id: cordelia$  |
    And the stdout does not contain "Harbormaster"

  @wip
  Scenario: the same person on another comm is matched by email
    When a turn is submitted with:
      | key                | value                     |
      | id                 | tide-9                    |
      | input              | Leave harbor              |
      | session            | harbor                    |
      | from.kind          | handle                    |
      | from.comm          | semaphore                 |
      | from.id            | flag-22                   |
      | from.email         | cordelia@marigold.example |
      | from.authenticated | true                      |
    And isaac is run with "turns show tide-9"
    Then the stdout matches:
      | pattern                  |
      | from.kind: contact       |
      | (?m)^from\.id: cordelia$ |
      | from.comm: semaphore     |
      | from.handle: flag-22     |

  @wip
  Scenario: the comm and id win when the email belongs to someone else
    Given the isaac EDN file "config/contacts/hieronymus.edn" exists with:
      | path    | value                                    |
      | name    | Hieronymus Finch                         |
      | handles | [{:email "hieronymus@marigold.example"}] |
    When a turn is submitted with:
      | key        | value                       |
      | id         | tide-9                      |
      | input      | Leave harbor                |
      | session    | harbor                      |
      | from.kind  | handle                      |
      | from.comm  | logbook                     |
      | from.id    | cordelia-7                  |
      | from.email | hieronymus@marigold.example |
    And isaac is run with "turns show tide-9"
    Then the stdout matches:
      | pattern                  |
      | from.kind: contact       |
      | (?m)^from\.id: cordelia$ |

  @wip
  Scenario: a handle no contact lists is stored as it arrived
    When a turn is submitted with:
      | key       | value        |
      | id        | tide-9       |
      | input     | Leave harbor |
      | session   | harbor       |
      | from.kind | handle       |
      | from.comm | logbook      |
      | from.id   | stranger-1   |
    And isaac is run with "turns show tide-9"
    Then the stdout matches:
      | pattern                    |
      | from.kind: handle          |
      | (?m)^from\.id: stranger-1$ |

  @wip
  Scenario: a contact matched on an unverified handle stays unverified
    When a turn is submitted with:
      | key                | value                     |
      | id                 | tide-9                    |
      | input              | Leave harbor              |
      | session            | harbor                    |
      | from.kind          | handle                    |
      | from.comm          | semaphore                 |
      | from.id            | flag-22                   |
      | from.email         | cordelia@marigold.example |
      | from.authenticated | false                     |
    And isaac is run with "turns show tide-9"
    Then the stdout matches:
      | pattern                   |
      | from.kind: contact        |
      | (?m)^from\.id: cordelia$  |
      | from.authenticated: false |
