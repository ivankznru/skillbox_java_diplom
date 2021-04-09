CREATE TABLE `users` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `is_moderator` int NOT NULL,
  `reg_time` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `code` varchar(255),
  `photo` text
);

CREATE TABLE `posts` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `is_active` tinyint NOT NULL,
  `status` ENUM ('NEW', 'ACCEPTED', 'DECLINE'),
  `moderator_id` int,
  `user_id` int,
  `time` datetime NOT NULL,
  `title` varchar(255) NOT NULL,
  `text` text NOT NULL,
  `view_count` int NOT NULL
);

CREATE TABLE `post_votes` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `time` datetime NOT NULL,
  `value` tinyint NOT NULL
);

CREATE TABLE `tags` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL
);

CREATE TABLE `tag2post` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `tag_id` int NOT NULL
);

CREATE TABLE `post_comments` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `parent_id` int,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `time` datetime NOT NULL,
  `text` text NOT NULL
);

CREATE TABLE `captcha_codes` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `code` tinytext NOT NULL,
  `secret_code` tinytext NOT NULL
);

CREATE TABLE `global_settings` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL
);

ALTER TABLE `posts` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `post_votes` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `post_votes` ADD FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`);

ALTER TABLE `tag2post` ADD FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`);

ALTER TABLE `tag2post` ADD FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`);

ALTER TABLE `post_comments` ADD FOREIGN KEY (`parent_id`) REFERENCES `post_comments` (`id`);

ALTER TABLE `post_comments` ADD FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`);

ALTER TABLE `post_comments` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
