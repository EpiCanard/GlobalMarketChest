PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

ALTER TABLE `{table_shops}` RENAME TO `{table_shops}_old`;

CREATE TABLE IF NOT EXISTS `{table_shops}`
(
    `id`            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `owner`         TEXT                              NOT NULL,
    `signLocation`  TEXT,
    `otherLocation` TEXT,
    `tpLocation`    TEXT,
    `type`          TINYINT(1)                        NOT NULL,
    `group`         VARCHAR(50)                       NOT NULL,
    `server`        VARCHAR(50)                       NOT NULL      DEFAULT 'default'
);

INSERT INTO `{table_shops}` (`id`, `owner`, `signLocation`, `otherLocation`, `type`, `group`, `server`) SELECT `id`, `owner`, `signLocation`, `otherLocation`, `type`, `group`, `server` FROM `{table_shops}_old`;

UPDATE `{table_shops}` SET signLocation = NULL WHERE signLocation = "";
UPDATE `{table_shops}` SET otherLocation = NULL WHERE otherLocation = "";

DROP TABLE `{table_shops}_old`;

COMMIT;

PRAGMA foreign_keys=on;
