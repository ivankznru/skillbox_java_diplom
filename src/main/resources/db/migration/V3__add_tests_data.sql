insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
VALUES ('1', '1', '2021-04-09 17:14:07', 'Vladimir', 'vkotov@vfemail.net', '123', '321', 'photo');
insert into users(is_moderator, reg_time, name, email, password, code, photo)
VALUES ('0', '2021-04-09 17:16:07', 'Petya', 'petya@vfemail.net', '123', '321', 'photo');
insert into posts(id, is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', '1', 'NEW', 1, 1, '2021-04-09 18:00:07', 'О миграции БД',
        'Честно скажу, мой кругозор, существенно расширился, когда я узнал от своего преподавателя, о таком важном и удобном понятии как миграция БД. Потратив пару часов времени и разобравшись как эта технология работает на практике, я перешёл на новый уровень работы со сложными проектами, теперь мне не составит труда быстро развернуть БД и по необходимости, внести правки в структуру БД и в данные этой самой БД. Я искренне советую всем читателям не знакомым с данной технологией, срочно брать в руки гугл и изучать её. А вот ссылка, достаточная для первого знакомства: https://www.youtube.com/watch?v=p9zVzPmOSnc',
        '1');
insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 2, '2021-04-09 18:01:07', 'Мысли о севреной столице России Санкт-Петербурге',
        'Друзья, кто не был ни разу в Северной столице России, рекомендую, прокатиться в ближайшие выходные в этот славный город, вы обязательно порадуетесь вместе с местными жителями обилию серых тонов в городском пейзаже, вы будете удивлены, количеству оттенков серого, коих гораздо больше чем 50, вы поднимете свой культурный уровень поговорив с местными жителями, вы просто качественно проведете свой досуг. Незабываемые впечатления.',
        '3');
INSERT INTO post_comments(id, parent_id, post_id, user_id, time, text) VALUES ('1',NULL,1,2,'2021-04-10 10:57:07','согласен с предыдущим оратором');
INSERT INTO post_comments(parent_id, post_id, user_id, time, text) VALUES (NULL,2,1,'2021-04-10 11:00:07','согласен с предыдущим оратором');
INSERT INTO tags(id, name) VALUES ('1','java');
INSERT INTO tags(name) VALUES ('спб');