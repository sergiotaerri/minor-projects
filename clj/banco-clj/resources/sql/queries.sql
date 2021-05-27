-- :name create-record! :! :1
-- :doc creates a new record for some given resource
INSERT INTO :i:resource
(:i*:fields)
VALUES :tuple:values
RETURNING *

-- :name update-record! :! :n
-- :doc updates record of key-name = key for some given resource
UPDATE :i:resource
SET (:i*:fields) = row(:tuple:values)
WHERE :i:key-name = :key

-- :name delete-record! :! :n
-- :doc delete record of key-name = key for some given resource
DELETE FROM :i:resource
WHERE WHERE :i:key-name = :key

-- :name get-record :? :1
-- :doc retrieve a given record by key-name = key for some resource
SELECT * FROM :i:resource
WHERE :i:key-name = :key

-- :name get-all-records :? :*
-- :doc retrieve all records of a given resource.
SELECT * FROM :i:resource

-- :name find-records-by :? :1
-- :doc finds records of a given resource whose fields matches the given values
SELECT * FROM :i:resource
WHERE (:i*:fields) = :tuple:values
