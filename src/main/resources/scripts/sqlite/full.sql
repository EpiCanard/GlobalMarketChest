CREATE TABLE IF NOT EXISTS `{table_auctions}`
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

CREATE TABLE IF NOT EXISTS `{table_shops}`
(
    `id`            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `owner`         TEXT                              NOT NULL,
    `signLocation`  TEXT,
    `otherLocation` TEXT,
    `tpLocation`    TEXT,
    `type`          TINYINT(1)                        NOT NULL,
    `mode`          TINYINT(1)                        NOT NULL      DEFAULT 2,
    `group`         VARCHAR(50)                       NOT NULL,
    `server`        VARCHAR(50)                       NOT NULL      DEFAULT 'default'
);

CREATE TABLE IF NOT EXISTS `{table_patches}`
(
    `id`    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `patch` VARCHAR(30)                       NOT NULL
);
