ALTER TABLE `{table_shops}` ADD server VARCHAR(50) NOT NULL DEFAULT 'default';

UPDATE `{table_shops}` SET server = 'default';

CREATE TABLE IF NOT EXISTS `{table_shops}_new`
(
    `id`            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `owner`         TEXT                              NOT NULL,
    `signLocation`  TEXT                              NOT NULL,
    `otherLocation` TEXT                              NOT NULL,
    `type`          TINYINT(1)                        NOT NULL,
    `group`         VARCHAR(50)                       NOT NULL,
    `server`        VARCHAR(50)                       NOT NULL      DEFAULT 'default'
);

INSERT INTO {table_shops}_new(`id`, `owner`, `signLocation`, `otherLocation`, `type`, `group`, `server`)
SELECT `id`, `owner`, `signLocation`, `otherLocation`, `type`, `group`, `server` FROM {table_shops};

DROP TABLE `{table_shops}`;
ALTER TABLE `{table_shops}_new` RENAME TO `{table_shops}`;
