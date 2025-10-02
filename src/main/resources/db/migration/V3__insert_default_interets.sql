-- Ensure default interests exist
INSERT INTO interet (nom)
SELECT 'Sport'
WHERE NOT EXISTS (SELECT 1 FROM interet WHERE nom = 'Sport');

INSERT INTO interet (nom)
SELECT 'Foot'
WHERE NOT EXISTS (SELECT 1 FROM interet WHERE nom = 'Foot');

INSERT INTO interet (nom)
SELECT 'Politique'
WHERE NOT EXISTS (SELECT 1 FROM interet WHERE nom = 'Politique');