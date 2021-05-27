-- :name drugs :? :*
-- :doc Get all drugs
SELECT * FROM drugs;

-- :name delete-drug :! :n
-- :doc delete a drug
DELETE FROM drugs WHERE id = :id;

-- :name insert-drug :! :n
-- :doc return a single returning row count
INSERT INTO drugs (name, availability, price)
VALUES (:name, :availability, :price) RETURNING id;

-- :name insert-drugs :! :n
-- :doc insert a bunch of characters utilizing *val
INSERT INTO drugs (name, availability, price)
VALUES (:tuple*drugs);

-- :name update-drug :! :n
-- :doc update a single drug by id
UPDATE drugs
SET name = :name, availability = :availability, price = :price
WHERE id = :id RETURNING id;
