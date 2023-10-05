INSERT INTO users ( name, email)
VALUES ( 'name1', 'email1@mail.ru'),
( 'name2', 'email2@mail.ru'),
( 'name3', 'email3@mail.ru'),
( 'name4', 'email4@mail.ru');

INSERT INTO categories (name)
VALUES ('category1'),
('category2'),
('category3'),
('category4');

INSERT INTO events (title, annotation, description, category_id, event_date, lon, lat, paid, request_moderation, participant_limit, state, initiator_id, created_on, published_on, admin_comment)
VALUES ('title1', 'annotation1', 'newdescription1', 1, '2025-08-08 12:00:00', 37.62, 55.754167, true, true, 2, 'PENDING', 1, '2020-08-08 12:00:00', null, null),
('title2', 'annotation2', 'NewDescription2', 2, '2025-08-01 12:00:00', 38.62, 50.754, true, true, 0, 'PUBLISHED', 1, '2020-09-08 12:00:00', '2020-09-09 12:00:00', 'some admin comment2'),
('title3', 'annotation3', 'newdescription3', 1, '2025-10-08 12:00:00', 30.62, 55.7541, false, false, 2, 'PUBLISHED', 1, '2020-09-10 12:00:00', '2020-09-12 12:00:00', 'some admin comment3'),
('title4', 'annotation4', 'Description4', 2, '2025-10-08 12:00:00', 31.62, 56.754167, false, false, 0, 'PUBLISHED', 1, '2020-09-11 12:00:00', '2020-09-12 12:00:00', null),
('title5', 'annotation5', 'newdescription5', 3, '2025-08-11 12:00:00', 34.62, 58.75, true, true, 2, 'PENDING', 1, '2020-08-15 12:00:00', null, 'some admin comment5'),
('title6', 'annotation6', 'newdescription6', 1, '2025-08-08 12:00:00', 37.62, 55.754167, true, true, 2, 'PUBLISHED', 1, '2020-08-08 12:00:00', null, null),
('title7', 'annotation7', 'NewDescription7', 2, '2025-09-08 12:00:00', 38.62, 50.754, true, true, 0, 'PUBLISHED', 1, '2020-09-08 12:00:00', '2020-09-09 12:00:00', null);

INSERT INTO requests (event_id, requester_id, created, status)
VALUES (1,2, '2020-08-08 12:00:00', 'CONFIRMED'),
(1,3, '2020-08-08 12:00:00', 'CONFIRMED'),
(1,4, '2020-08-08 12:00:00', 'PENDING'),
(2,2, '2020-08-08 12:00:00', 'CONFIRMED'),
(3,4, '2020-08-08 12:00:00', 'PENDING'),
(2,4, '2020-08-08 12:00:00', 'CANCELED'),
(6,4, '2020-08-08 12:00:00', 'CONFIRMED');

INSERT INTO compilations (title, pinned)
VALUES ('Compilation1', true),
('Compilation2', false),
('Compilation3', false),
('Compilation4', true),
('Compilation5', true);

INSERT INTO compilations_events (compilation_id, event_id)
VALUES (1, 2),
(1, 3),
(1, 7),
(2, 2),
(3, 2),
(3, 4),
(4, 2),
(4, 3),
(4, 6);