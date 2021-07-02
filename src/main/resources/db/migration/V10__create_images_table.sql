SET AUTO_INCREMENT_INCREMENT = 1;
CREATE TABLE `images`
(
    `id`         int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `image`      mediumblob      NOT NULL,
    `image_name` varchar(70)     NOT NULL
);