ALTER TABLE `{table_auctions}` ADD status TINYINT(1) NOT NULL DEFAULT 0;

UPDATE `{table_auctions}` SET status = 2 WHERE playerStarter = playerEnder AND ended = TRUE;
UPDATE `{table_auctions}` SET status = 1 WHERE playerStarter != playerEnder AND ended = TRUE;

ALTER TABLE `{table_auctions}` DROP COLUMN ended;

CREATE TABLE IF NOT EXISTS `{table_patches}`
(
    `id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `patch` VARCHAR(30) NOT NULL
);