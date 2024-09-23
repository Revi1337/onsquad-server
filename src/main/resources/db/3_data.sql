insert into member(email, address, address_detail,  nickname, password, introduce, kakao_link, profile_image, created_at, updated_at)
values ('andong@fuck.com', '어딘가1', '상세한 어딘가1', '닉네임1', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('kyounghak@fuck.com', '어딘가2', '상세한 어딘가2', '닉네임2', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('kwangwon@fuck.com', '어딘가3', '상세한 어딘가3', '닉네임3', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('hijin@fuck.com', '어딘가4', '상세한 어딘가4', '닉네임4', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('yoonji@fuck.com', '어딘가5', '상세한 어딘가5', '닉네임5', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('anonymous@fuck.com', '어딘가6', '상세한 어딘가6', '닉네임6', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy1@fuck.com', '어딘가7', '상세한 어딘가7', '닉네임7', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy2@fuck.com', '어딘가8', '상세한 어딘가8', '닉네임8', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy3@fuck.com', '어딘가9', '상세한 어딘가9', '닉네임9', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy4@fuck.com', '어딘가10', '상세한 어딘가10', '닉네임10', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy5@fuck.com', '어딘가11', '상세한 어딘가11', '닉네임11', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnq_LG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('dummy6@fuck.com', '어딘가12', '상세한 어딘가12', '닉네임12', '{bcrypt}$2a$10$9qzyli03H7Z2dsPn3e56Su5CoOolFJApvRS86AA7d8cSvuBwnqLG.', '', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

insert into image(image_url)
values ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg'),
       ('https://pbs.twimg.com/media/FO70Y53WYAwGmz0.jpg');

insert into crew(name, introduce, detail, kakao_link, image_id, member_id, created_at, updated_at)
values ('크루 이름 1', '크루 소개 1', '크루 디테일 1', 'https://카카오링크.com', 1, 1, CURRENT_TIMESTAMP + 1, CURRENT_TIMESTAMP + 1),
       ('크루 이름 2', '크루 소개 2', '크루 디테일 2', 'https://카카오링크.com', 2, 1, CURRENT_TIMESTAMP + 2, CURRENT_TIMESTAMP + 2),
       ('크루 이름 3', '크루 소개 3', '크루 디테일 3', 'https://카카오링크.com', 3, 1, CURRENT_TIMESTAMP + 3, CURRENT_TIMESTAMP + 3),
       ('크루 이름 4', '크루 소개 4', '크루 디테일 4', 'https://카카오링크.com', 4, 1, CURRENT_TIMESTAMP + 4, CURRENT_TIMESTAMP + 4),

       ('크루 이름 5', '크루 소개 5', '크루 디테일 5', 'https://카카오링크.com', 5, 2, CURRENT_TIMESTAMP + 5, CURRENT_TIMESTAMP + 5),
       ('크루 이름 6', '크루 소개 6', '크루 디테일 6', 'https://카카오링크.com', 6, 2, CURRENT_TIMESTAMP + 6, CURRENT_TIMESTAMP + 6),
       ('크루 이름 7', '크루 소개 7', '크루 디테일 7', 'https://카카오링크.com', 7, 2, CURRENT_TIMESTAMP + 7, CURRENT_TIMESTAMP + 7),
       ('크루 이름 8', '크루 소개 8', '크루 디테일 8', 'https://카카오링크.com', 8, 2, CURRENT_TIMESTAMP + 8, CURRENT_TIMESTAMP + 8),

       ('크루 이름 9', '크루 소개 9', '크루 디테일 9', 'https://카카오링크.com', 9, 3, CURRENT_TIMESTAMP + 9, CURRENT_TIMESTAMP + 9),
       ('크루 이름 10', '크루 소개 10', '크루 디테일 10', 'https://카카오링크.com', 10, 3, CURRENT_TIMESTAMP + 10, CURRENT_TIMESTAMP + 10),
       ('크루 이름 11', '크루 소개 11', '크루 디테일 11', 'https://카카오링크.com', 11, 3, CURRENT_TIMESTAMP + 11, CURRENT_TIMESTAMP + 11),
       ('크루 이름 12', '크루 소개 12', '크루 디테일 12', 'https://카카오링크.com', 12, 3, CURRENT_TIMESTAMP + 12, CURRENT_TIMESTAMP + 12),

       ('크루 이름 13', '크루 소개 13', '크루 디테일 13', 'https://카카오링크.com', 13, 4, CURRENT_TIMESTAMP + 13, CURRENT_TIMESTAMP + 13),
       ('크루 이름 14', '크루 소개 14', '크루 디테일 14', 'https://카카오링크.com', 14, 4, CURRENT_TIMESTAMP + 14, CURRENT_TIMESTAMP + 14),
       ('크루 이름 15', '크루 소개 15', '크루 디테일 15', 'https://카카오링크.com', 15, 4, CURRENT_TIMESTAMP + 15, CURRENT_TIMESTAMP + 15),
       ('크루 이름 16', '크루 소개 16', '크루 디테일 16', 'https://카카오링크.com', 16, 4, CURRENT_TIMESTAMP + 16, CURRENT_TIMESTAMP + 16),

       ('크루 이름 17', '크루 소개 17', '크루 디테일 17', 'https://카카오링크.com', 17, 5, CURRENT_TIMESTAMP + 17, CURRENT_TIMESTAMP + 17),
       ('크루 이름 18', '크루 소개 18', '크루 디테일 18', 'https://카카오링크.com', 18, 5, CURRENT_TIMESTAMP + 18, CURRENT_TIMESTAMP + 18),
       ('크루 이름 19', '크루 소개 19', '크루 디테일 19', 'https://카카오링크.com', 19, 5, CURRENT_TIMESTAMP + 19, CURRENT_TIMESTAMP + 19),
       ('크루 이름 20', '크루 소개 20', '크루 디테일 20', 'https://카카오링크.com', 20, 5, CURRENT_TIMESTAMP + 20, CURRENT_TIMESTAMP + 20);

insert into crew_hashtag(crew_id, hashtag_id)
values (1, 1), (1, 2), (1, 3),
       (2, 4), (2, 5), (2, 6),
       (3, 7), (3, 8), (3, 9),
       (4, 10), (4, 11), (4, 12),

       (5, 13), (5, 14), (5, 15),
       (6, 16), (6, 17), (6, 18),
       (7, 19), (7, 20), (7, 21),
       (8, 22), (8, 23), (8, 24),

       (9, 25), (9, 26), (9, 27),
       (10, 28), (10, 29), (10, 30),
       (11, 31), (11, 32), (11, 33),
       (12, 34), (12, 35), (12, 36),

       (13, 37), (13, 38), (13, 39),
       (14, 40), (14, 1), (14, 2), (14, 3),
       (15, 3), (15, 4), (15, 5),
       (16, 6), (16, 7), (16, 8),

       (17, 9), (17, 10), (17, 11),
       (18, 12), (18, 13), (18, 14),
       (19, 15), (19, 16), (19, 17),
       (20, 18), (20, 19), (20, 20);

insert into crew_member(crew_id, member_id, role, participate_at)
values (1, 1, 'OWNER', CURRENT_TIMESTAMP + 1), (1, 2, 'GENERAL', CURRENT_TIMESTAMP + 2), (1, 3, 'GENERAL', CURRENT_TIMESTAMP + 3), (1, 4, 'GENERAL', CURRENT_TIMESTAMP + 4), (1, 5, 'GENERAL', CURRENT_TIMESTAMP + 5),
       (2, 1, 'OWNER', CURRENT_TIMESTAMP + 6), (2, 2, 'GENERAL', CURRENT_TIMESTAMP + 7), (2, 3, 'GENERAL', CURRENT_TIMESTAMP + 8), (2, 4, 'GENERAL', CURRENT_TIMESTAMP + 9), (2, 5, 'GENERAL', CURRENT_TIMESTAMP + 10),
       (3, 1, 'OWNER', CURRENT_TIMESTAMP + 11), (3, 2, 'GENERAL', CURRENT_TIMESTAMP + 12), (3, 3, 'GENERAL', CURRENT_TIMESTAMP + 13), (3, 4, 'GENERAL', CURRENT_TIMESTAMP + 14), (3, 5, 'GENERAL', CURRENT_TIMESTAMP + 15),
       (4, 1, 'OWNER', CURRENT_TIMESTAMP + 16), (4, 2, 'GENERAL', CURRENT_TIMESTAMP + 17), (4, 3, 'GENERAL', CURRENT_TIMESTAMP + 18), (4, 4, 'GENERAL', CURRENT_TIMESTAMP + 19), (4, 5, 'GENERAL', CURRENT_TIMESTAMP + 20),

       (5, 1, 'GENERAL', CURRENT_TIMESTAMP + 21), (5, 2, 'OWNER', CURRENT_TIMESTAMP + 22), (5, 3, 'GENERAL', CURRENT_TIMESTAMP + 23), (5, 4, 'GENERAL', CURRENT_TIMESTAMP + 24), (5, 5, 'GENERAL', CURRENT_TIMESTAMP + 25),
       (6, 1, 'GENERAL', CURRENT_TIMESTAMP + 26), (6, 2, 'OWNER', CURRENT_TIMESTAMP + 27), (6, 3, 'GENERAL', CURRENT_TIMESTAMP + 28), (6, 4, 'GENERAL', CURRENT_TIMESTAMP + 29), (6, 5, 'GENERAL', CURRENT_TIMESTAMP + 30),
       (7, 1, 'GENERAL', CURRENT_TIMESTAMP + 31), (7, 2, 'OWNER', CURRENT_TIMESTAMP + 32), (7, 3, 'GENERAL', CURRENT_TIMESTAMP + 33), (7, 4, 'GENERAL', CURRENT_TIMESTAMP + 34), (7, 5, 'GENERAL', CURRENT_TIMESTAMP + 35),
       (8, 1, 'GENERAL', CURRENT_TIMESTAMP + 36), (8, 2, 'OWNER', CURRENT_TIMESTAMP + 37), (8, 3, 'GENERAL', CURRENT_TIMESTAMP + 38), (8, 4, 'GENERAL', CURRENT_TIMESTAMP + 39), (8, 5, 'GENERAL', CURRENT_TIMESTAMP + 40),

       (9, 1, 'GENERAL', CURRENT_TIMESTAMP + 41), (9, 2, 'GENERAL', CURRENT_TIMESTAMP + 45), (9, 3, 'OWNER', CURRENT_TIMESTAMP + 49), (9, 4, 'GENERAL', CURRENT_TIMESTAMP + 53), (9, 5, 'GENERAL', CURRENT_TIMESTAMP + 57),
       (10, 1, 'GENERAL', CURRENT_TIMESTAMP + 42), (10, 2, 'GENERAL', CURRENT_TIMESTAMP + 46), (10, 3, 'OWNER', CURRENT_TIMESTAMP + 50), (10, 4, 'GENERAL', CURRENT_TIMESTAMP + 54), (10, 5, 'GENERAL', CURRENT_TIMESTAMP + 58),
       (11, 1, 'GENERAL', CURRENT_TIMESTAMP + 43), (11, 2, 'GENERAL', CURRENT_TIMESTAMP + 47), (11, 3, 'OWNER', CURRENT_TIMESTAMP + 51), (11, 4, 'GENERAL', CURRENT_TIMESTAMP + 55), (11, 5, 'GENERAL', CURRENT_TIMESTAMP + 59),
       (12, 1, 'GENERAL', CURRENT_TIMESTAMP + 44), (12, 2, 'GENERAL', CURRENT_TIMESTAMP + 48), (12, 3, 'OWNER', CURRENT_TIMESTAMP + 52), (12, 4, 'GENERAL', CURRENT_TIMESTAMP + 56), (12, 5, 'GENERAL', CURRENT_TIMESTAMP + 60),

       (13, 1, 'GENERAL', CURRENT_TIMESTAMP + 61), (13, 2, 'GENERAL', CURRENT_TIMESTAMP + 65), (13, 3, 'GENERAL', CURRENT_TIMESTAMP + 69), (13, 4, 'OWNER', CURRENT_TIMESTAMP + 73), (13, 5, 'GENERAL', CURRENT_TIMESTAMP + 77),
       (14, 1, 'GENERAL', CURRENT_TIMESTAMP + 62), (14, 2, 'GENERAL', CURRENT_TIMESTAMP + 66), (14, 3, 'GENERAL', CURRENT_TIMESTAMP + 70), (14, 4, 'OWNER', CURRENT_TIMESTAMP + 74), (14, 5, 'GENERAL', CURRENT_TIMESTAMP + 78),
       (15, 1, 'GENERAL', CURRENT_TIMESTAMP + 63), (15, 2, 'GENERAL', CURRENT_TIMESTAMP + 67), (15, 3, 'GENERAL', CURRENT_TIMESTAMP + 71), (15, 4, 'OWNER', CURRENT_TIMESTAMP + 75), (15, 5, 'GENERAL', CURRENT_TIMESTAMP + 79),
       (16, 1, 'GENERAL', CURRENT_TIMESTAMP + 64), (16, 2, 'GENERAL', CURRENT_TIMESTAMP + 68), (16, 3, 'GENERAL', CURRENT_TIMESTAMP + 72), (16, 4, 'OWNER', CURRENT_TIMESTAMP + 76), (16, 5, 'GENERAL', CURRENT_TIMESTAMP + 80),

       (17, 1, 'GENERAL', CURRENT_TIMESTAMP + 81), (17, 2, 'GENERAL', CURRENT_TIMESTAMP + 85), (17, 3, 'GENERAL', CURRENT_TIMESTAMP + 89), (17, 4, 'GENERAL', CURRENT_TIMESTAMP + 93), (17, 5, 'OWNER', CURRENT_TIMESTAMP + 97),
       (18, 1, 'GENERAL', CURRENT_TIMESTAMP + 82), (18, 2, 'GENERAL', CURRENT_TIMESTAMP + 86), (18, 3, 'GENERAL', CURRENT_TIMESTAMP + 90), (18, 4, 'GENERAL', CURRENT_TIMESTAMP + 94), (18, 5, 'OWNER', CURRENT_TIMESTAMP + 98),
       (19, 1, 'GENERAL', CURRENT_TIMESTAMP + 83), (19, 2, 'GENERAL', CURRENT_TIMESTAMP + 87), (19, 3, 'GENERAL', CURRENT_TIMESTAMP + 91), (19, 4, 'GENERAL', CURRENT_TIMESTAMP + 95), (19, 5, 'OWNER', CURRENT_TIMESTAMP + 99),
       (20, 1, 'GENERAL', CURRENT_TIMESTAMP + 84), (20, 2, 'GENERAL', CURRENT_TIMESTAMP + 88), (20, 3, 'GENERAL', CURRENT_TIMESTAMP + 92), (20, 4, 'GENERAL', CURRENT_TIMESTAMP + 96), (20, 5, 'OWNER', CURRENT_TIMESTAMP + 100);

insert into squad(title, content, capacity, remain, address, address_detail, kakao_link, discord_link, created_at, updated_at, crew_member_id, crew_id)
values ('스쿼드 타이틀 1', '스쿼드 본문 1', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
       ('스쿼드 타이틀 2', '스쿼드 본문 2', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 1),

       ('스쿼드 타이틀 3', '스쿼드 본문 3', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7, 2),
       ('스쿼드 타이틀 4', '스쿼드 본문 4', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 2),

       ('스쿼드 타이틀 5', '스쿼드 본문 5', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 13, 3),
       ('스쿼드 타이틀 6', '스쿼드 본문 6', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 15, 3),

       ('스쿼드 타이틀 7', '스쿼드 본문 7', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 16, 4),
       ('스쿼드 타이틀 8', '스쿼드 본문 8', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 18, 4),

       ('스쿼드 타이틀 9', '스쿼드 본문 9', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 21, 5),
       ('스쿼드 타이틀 10', '스쿼드 본문 10', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 22, 5),

       ('스쿼드 타이틀 11', '스쿼드 본문 11', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 28, 6),
       ('스쿼드 타이틀 12', '스쿼드 본문 12', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 29, 6),

       ('스쿼드 타이틀 13', '스쿼드 본문 13', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 35, 7),
       ('스쿼드 타이틀 14', '스쿼드 본문 14', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 31, 7),

       ('스쿼드 타이틀 15', '스쿼드 본문 15', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 37, 8),
       ('스쿼드 타이틀 16', '스쿼드 본문 16', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 38, 8),

       ('스쿼드 타이틀 17', '스쿼드 본문 17', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 41, 9),
       ('스쿼드 타이틀 18', '스쿼드 본문 18', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 42, 9),

       ('스쿼드 타이틀 19', '스쿼드 본문 19', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 48, 10),
       ('스쿼드 타이틀 20', '스쿼드 본문 20', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 49, 10),

       ('스쿼드 타이틀 21', '스쿼드 본문 21', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 55, 11),
       ('스쿼드 타이틀 22', '스쿼드 본문 22', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, 11),

       ('스쿼드 타이틀 23', '스쿼드 본문 23', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 57, 12),
       ('스쿼드 타이틀 24', '스쿼드 본문 24', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 58, 12),

       ('스쿼드 타이틀 25', '스쿼드 본문 25', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 61, 13),
       ('스쿼드 타이틀 26', '스쿼드 본문 26', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 62, 13),

       ('스쿼드 타이틀 27', '스쿼드 본문 27', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 68, 14),
       ('스쿼드 타이틀 28', '스쿼드 본문 28', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 69, 14),

       ('스쿼드 타이틀 29', '스쿼드 본문 29', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 75, 15),
       ('스쿼드 타이틀 30', '스쿼드 본문 30', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 71, 15),

       ('스쿼드 타이틀 31', '스쿼드 본문 31', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 77, 16),
       ('스쿼드 타이틀 32', '스쿼드 본문 32', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 78, 16),

       ('스쿼드 타이틀 33', '스쿼드 본문 33', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 81, 17),
       ('스쿼드 타이틀 34', '스쿼드 본문 34', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 82, 17),

       ('스쿼드 타이틀 35', '스쿼드 본문 35', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 88, 18),
       ('스쿼드 타이틀 36', '스쿼드 본문 36', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 89, 18),

       ('스쿼드 타이틀 37', '스쿼드 본문 37', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 95, 19),
       ('스쿼드 타이틀 38', '스쿼드 본문 38', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 91, 19),

       ('스쿼드 타이틀 39', '스쿼드 본문 39', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 97, 20),
       ('스쿼드 타이틀 40', '스쿼드 본문 40', 8, 7, '어딘가', '상세한 어딘가', 'https://카카오링크.com', 'https://디스코드.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 98, 20);

insert into squad_category(squad_id, category_id)
values (1, 1),
       (2, 3), (2, 4),
       (3, 5), (3, 6),
       (4, 7), (4, 8),
       (5, 9), (5, 10),
       (6, 11), (6, 12),
       (7, 13), (7, 14),
       (8, 15), (8, 16),
       (9, 17), (9, 18),
       (10, 19), (10, 20),
       (11, 21), (11, 22),
       (12, 23), (12, 24),
       (13, 25), (13, 26),
       (14, 27), (14, 28),
       (15, 29), (15, 30),
       (16, 31), (16, 32),
       (17, 33), (17, 34),
       (18, 1),
       (19, 2), (19, 3),
       (20, 4), (20, 5),
       (21, 6), (21, 7),
       (22, 8), (22, 9),
       (23, 10), (23, 11),
       (24, 12), (24, 13),
       (25, 14), (25, 15),
       (26, 16), (26, 17),
       (27, 18), (27, 19),
       (28, 20), (28, 21),
       (29, 22), (29, 23),
       (30, 24), (30, 25),
       (31, 26), (31, 27),
       (32, 28), (32, 29),
       (33, 30), (33, 31),
       (34, 32), (34, 33),
       (35, 34), (35, 35),
       (36, 1),
       (37, 3), (37, 4),
       (38, 5), (38, 6),
       (39, 7), (39, 8),
       (40, 9), (40, 10);

insert into squad_member(squad_id, crew_member_id, role, status, participate_at)
values (1, 1, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP), (1, 2, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (2, 5, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (3, 7, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (4, 9, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (5, 13, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (6, 15, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (7, 16, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (8, 18, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (9, 21, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (10, 22, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (11, 28, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (12, 29, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (14, 31, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (13, 35, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (15, 37, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (16, 38, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (17, 41, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (18, 42, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (19, 48, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (20, 49, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (22, 51, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (21, 55, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (23, 57, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (24, 58, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (25, 61, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (26, 62, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (27, 68, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (28, 69, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (30, 71, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (29, 75, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (31, 77, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (32, 78, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (33, 81, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (34, 82, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (35, 88, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (36, 89, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (38, 91, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (37, 95, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (39, 97, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP),
       (40, 98, 'LEADER', 'ACCEPT', CURRENT_TIMESTAMP);
