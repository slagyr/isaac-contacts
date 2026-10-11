# Contacts

A contact is a person known by name across comms. Keep one EDN file per person
under `config/contacts/`, named for the contact id:

```edn
;; config/contacts/cordelia.edn
{:name "Cordelia Vane"
 :title "Harbormaster"
 :weight 3
 :handles [{:comm :logbook :id "cordelia-7"}
           {:email "cordelia@marigold.example"}]}
```

`:name` is required. `:title` and `:weight` (integer) are optional metadata;
neither appears on a turn. Each `:handles` entry is either a comm keyword and
id string, or an email string. Contacts can have no handles. Two contacts may
not claim the same comm/id pair or email (email comparisons ignore case): config
loading reports both ids and the conflicting handle.

At turn submission the identifier looks up a `:kind :handle` party against the
loaded config snapshot. It first matches `:comm` plus `:id`; if none matches,
it matches `:email` case-insensitively. A match replaces the handle with a
contact reference:

```edn
{:kind :contact :id "cordelia" :name "Cordelia Vane"
 :comm :logbook :handle "cordelia-7"}
```

`:comm` and `:handle` preserve the incoming comm and handle id. The agent
preserves the incoming `:authenticated` claim; a contact name is not proof of
identity. Look up the contact by id for its title and weight. An unmatched
handle remains unchanged. The identifier does no I/O while identifying.
