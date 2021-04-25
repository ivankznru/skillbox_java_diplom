insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
VALUES ('1', '1', '2021-04-09 17:14:07', 'Vladimir', 'vkotov@vfemail.net', '123', '321', 'C:/Users/admin/IdeaProjects/devpub/src/main/resources/avatars/ab/cd/ef/52461.jpg');
insert into users(is_moderator, reg_time, name, email, password, code, photo)
VALUES ('0', '2021-04-09 17:16:07', 'Petya', 'petya@vfemail.net', '123', '321', 'C:/Users/admin/IdeaProjects/devpub/src/main/resources/avatars/ab/cd/ef/52462.jpg');

insert into users(is_moderator, reg_time, name, email, password, code, photo)
VALUES ('0', '2021-04-10 16:11:07', 'Denya', 'denya@vfemail.net', '123', '321', 'avatars/ab/cd/ef/52463.jpg');

insert into users(is_moderator, reg_time, name, email, password, code, photo)
VALUES ('0', '2021-04-11 17:16:07', 'Vasya', 'Vasya@vfemail.net', '123', '321', 'avatars/ab/cd/ef/52464.jpg');

insert into users(is_moderator, reg_time, name, email, password, code, photo)
VALUES ('0', '2021-04-12 16:10:07', 'Sasha', 'sasha@vfemail.net', '123', '321', 'avatars/ab/cd/ef/52465.jpg');

insert into posts(id, is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', '1', 'NEW', 1, 1, '2020-12-31 18:00:07', 'О миграции БД',
        'Честно скажу, мой кругозор, существенно расширился, когда я узнал от своего преподавателя, о таком важном и удобном понятии как миграция БД. Потратив пару часов времени и разобравшись как эта технология работает на практике, я перешёл на новый уровень работы со сложными проектами, теперь мне не составит труда быстро развернуть БД и по необходимости, внести правки в структуру БД и в данные этой самой БД. Я искренне советую всем читателям не знакомым с данной технологией, срочно брать в руки гугл и изучать её. А вот ссылка, достаточная для первого знакомства: https://www.youtube.com/watch?v=p9zVzPmOSnc',
        '1');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 2, '2021-04-09 18:01:07', 'Мысли о северной столице России Санкт-Петербурге',
        'Друзья, кто не был ни разу в Северной столице России, рекомендую, прокатиться в ближайшие выходные в этот славный город, вы обязательно порадуетесь вместе с местными жителями обилию серых тонов в городском пейзаже, вы будете удивлены, количеству оттенков серого, коих гораздо больше чем 50, вы поднимете свой культурный уровень поговорив с местными жителями, вы просто качественно проведете свой досуг. Незабываемые впечатления.',
        '3');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('0', 'NEW', 1, 1, '2021-04-10 18:01:07', 'Spring Boot - возвращение JSON с массивом объектов',
        ' Используя этот ответ в качестве подсказки, я разработал контроллер Spring Boot для /greetings чтобы вернуть приветствие на разных языках в JSON. Пока я получаю вывод в нужном формате (массив объектов), не могли бы вы дать мне знать, если есть лучший способ? ', '2');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 3, '2021-04-11 18:01:07', 'Страна фермеров и банков: как живётся разработчику в крошечном Люксембурге',
        'Фронтенд-разработчице посчастливилось переехать в Люксембург. Сначала казалось, что это скучная бабушкина деревня, и делать тут нечего. Но через пару лет выяснилось, что у жизни в крошке-стране есть неожиданные плюсы.',
        '0');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 2, '2021-04-12 18:02:07', 'Книга «Современный скрапинг веб-сайтов с помощью Python. 2-е межд. издание»',
        'Привет, Хаброжители! Если программирование напоминает волшебство, то веб-скрапинг — это очень сильное колдунство. Написав простую автоматизированную программу, можно отправлять запросы на в.','1');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 4, '2021-04-12 18:03:07', 'За что вы так меня не любите?» (с) Python',
        'Python уже на протяжении нескольких лет держится в топе языков программирования. А любая популярная вещь так или иначе обречена на шквал критики разной степени конструктивности.','2');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 5, '2021-04-13 18:04:07', 'Как считывать и удалять метаданные из ваших фотографий с помощью Python',
        'Те, кто знает Python, могут делать поистине удивительные вещи, например, создавать арт-объекты и игры и красивые карты, полнотекстовую поисковую машину и систему распознавания лиц. Применим Python и для вполне бытовых задач. Сегодня, специально к.', '4');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 3, '2021-04-13 18:05:07', 'Исследование: какие способы обхода антивирусов используют хакеры',
        'Создание уникального вредоносного ПО требует больших ресурсов, поэтому многие хакерские группировки используют в своих атаках массовое, часто публично доступное ВПО.',
        '44');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 4, '2021-04-13 18:06:07', 'Kubernets 1.21 — неожиданно много изменений…',
        'Новая эмблема символизирует распределение членов команды выпуска релиза по земному шару  — от UTC-8 до UTC+8 (похоже, ни японцев, ни корейцев в команде нет). .',
        '54');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 5, '2021-04-14 18:07:07', 'Создаем Booking приложение с Webix UI',
        'Эта статья предназначена для тех, кто ценит свое время и не желает тратить многие месяцы на дотошное изучение нативных технологий web разработки.',
        '5454');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 1, '2021-04-15 18:08:07', 'Как я делал матчер правил возврата автобусных билетов',
        'Мы тут автоматизируем автобусы, недавно вот с нашей помощью все билеты в России стали электронными.',
        '55');

insert into posts(is_active, status, moderator_id, user_id, time, title, text, view_count)
VALUES ('1', 'NEW', 1, 2, '2021-04-15 18:09:07', 'Адаптация подхода с применением сжатия zlib для отсеивания некачественных текстов разной длины',
        'Недавно Сбер в статье Всё, что нам нужно — это генерация предложил интересный подход для отсеивания некачественных текстов (технического мусора и шаблонного спама). ',
        '33');

INSERT INTO post_comments(id, parent_id, post_id, user_id, time, text) VALUES ('1',NULL,1,2,'2021-04-10 10:57:07','согласен с предыдущим оратором');
INSERT INTO post_comments(parent_id, post_id, user_id, time, text) VALUES (NULL,2,1,'2021-04-10 11:00:07','согласен с предыдущим оратором');
INSERT INTO post_comments(parent_id, post_id, user_id, time, text) VALUES (NULL,2,1,'2021-04-19 17:48:07','согласен с предыдущим оратором 2');
INSERT INTO post_comments(parent_id, post_id, user_id, time, text) VALUES (NULL,3,1,'2021-04-20 15:40:07','не согласен с предыдущим оратором');

INSERT INTO tags(id, name) VALUES ('1','java');
INSERT INTO tags(name) VALUES ('спб');
INSERT INTO tags(name) VALUES ('vw');
INSERT INTO tags(name) VALUES ('antivirus');
INSERT INTO tags(name) VALUES ('sql');
INSERT INTO tags(name) VALUES ('spring');
INSERT INTO tags(name) VALUES ('python');
INSERT INTO tags(name) VALUES ('Kubernets');
INSERT INTO tags(name) VALUES ('Webix');
INSERT INTO tags(name) VALUES ('программирование');
INSERT INTO tags(name) VALUES ('zlib');

INSERT INTO tag2post(id, post_id, tag_id) VALUES ('1', 1, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (3, 2);
INSERT INTO tag2post(post_id, tag_id) VALUES (3, 3);
INSERT INTO tag2post(post_id, tag_id) VALUES (3, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (4, 3);
INSERT INTO tag2post(post_id, tag_id) VALUES (4, 4);
INSERT INTO tag2post(post_id, tag_id) VALUES (4, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (4, 3);
INSERT INTO tag2post(post_id, tag_id) VALUES (5, 5);
INSERT INTO tag2post(post_id, tag_id) VALUES (6, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (7, 5);
INSERT INTO tag2post(post_id, tag_id) VALUES (8, 6);
INSERT INTO tag2post(post_id, tag_id) VALUES (8, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (8, 8);
INSERT INTO tag2post(post_id, tag_id) VALUES (9, 7);
INSERT INTO tag2post(post_id, tag_id) VALUES (10, 1);
INSERT INTO tag2post(post_id, tag_id) VALUES (11, 2);

INSERT INTO post_votes(id, user_id, post_id, time, value) VALUES ('1', 2, 1, '2021-04-11 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 3, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 4, '2021-04-13 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 5, '2021-04-14 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 1, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 2, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 4, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 1, '2021-04-14 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 6, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 7, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (1, 5, '2021-04-14 18:09:07', '1');

INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 3, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 4, '2021-04-13 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 5, '2021-04-14 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 1, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 2, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 4, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 1, '2021-04-14 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 2, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 9, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (2, 5, '2021-04-14 18:09:07', '1');

INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 3, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 4, '2021-04-13 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 5, '2021-04-14 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 10, '2021-04-12 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 2, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (3, 4, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (4, 8, '2021-04-14 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (4, 2, '2021-04-12 18:09:07', '-1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (4, 3, '2021-04-13 18:09:07', '1');
INSERT INTO post_votes(user_id, post_id, time, value) VALUES (4, 5, '2021-04-14 18:09:07', '1');
