ALTER TABLE `{table_auctions}` ADD status TINYINT(1) NOT NULL DEFAULT 0;

UPDATE `{table_auctions}` SET status = 2 WHERE playerStarter = playerEnder AND ended = TRUE;
UPDATE `{table_auctions}` SET status = 1 WHERE playerStarter != playerEnder AND ended = TRUE;

CREATE TABLE IF NOT EXISTS `{table_auctions}_new`
(
    `id`            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `itemStack`     VARCHAR(50)                       NOT NULL,
    `itemMeta`      TEXT,
    `amount`        INT UNSIGNED                      NOT NULL,
    `price`         DOUBLE                            NOT NULL,
    `status`        TINYINT(1)                        NOT NULL DEFAULT 0,
    `type`          TINYINT(1)                        NOT NULL,
    `playerStarter` TEXT                              NOT NULL,
    `playerEnder`   TEXT                                       DEFAULT NULL,
    `start`         TIMESTAMP                                  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `end`           TIMESTAMP                                  DEFAULT '2000-01-01 00:00:01' NOT NULL,
    `group`         VARCHAR(50)                       NOT NULL
);

INSERT INTO {table_auctions} _new(`id`, `itemStack`, `itemMeta`, `amount`, `price`, `status`, `type`, `playerStarter`, `playerEnder`, `start`, `end`, `group`)
SELECT `id`, `itemStack`, `itemMeta`, `amount`, `price`, `status`, `type`, `playerStarter`, `playerEnder`, `start`, `end`, `group` FROM {table_auctions};

DROP TABLE `{table_auctions}`;
ALTER TABLE `{table_auctions}_new` RENAME TO `{table_auctions}`;

CREATE TABLE IF NOT EXISTS `{table_patches}`
(
    `id`    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `patch` VARCHAR(30)                       NOT NULL
);
