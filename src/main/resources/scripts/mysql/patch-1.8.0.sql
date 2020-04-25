ALTER TABLE `{table_shops}` ADD server VARCHAR(50) NOT NULL DEFAULT 'default';

UPDATE `{table_shops}` SET server = 'default';
