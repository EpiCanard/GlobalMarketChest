ALTER TABLE `{table_shops}` MODIFY COLUMN `signLocation` TEXT;
ALTER TABLE `{table_shops}` MODIFY COLUMN `otherLocation` TEXT;
ALTER TABLE `{table_shops}` ADD COLUMN `tpLocation` TEXT;

UPDATE `{table_shops}` SET signLocation = NULL WHERE signLocation = "";
UPDATE `{table_shops}` SET otherLocation = NULL WHERE otherLocation = "";

