DROP DATABASE IF EXISTS `Dashboard`;
CREATE DATABASE `Dashboard`;
USE `Dashboard`;






CREATE TABLE holidays (
  id INT NOT NULL,
  name VARCHAR(1000),
  local_name VARCHAR(1000),
  date DATE
);


INSERT INTO holidays (id, name, local_name, date)
VALUES
(1, "New Year's Day", "Den obnovy samostatného českého státu; Nový rok", "2025-01-01"),
(2, "Good Friday", "Velký pátek", "2025-04-18"),
(3, "Easter Monday", "Velikonoční pondělí", "2025-04-21");