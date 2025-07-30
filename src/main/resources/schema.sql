
DROP TABLE IF EXISTS holidays;

CREATE TABLE IF NOT EXISTS holidays (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(1000),
  local_name VARCHAR(1000),
  date DATE,
  country_code VARCHAR(10),
  fixed BOOLEAN,
  global BOOLEAN
);

CREATE TABLE IF NOT EXISTS notes (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  text VARCHAR(2000)
);

CREATE TABLE IF NOT EXISTS supported_currencies (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  symbol VARCHAR(10) NOT NULL,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS convert_rate (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  from_currency_id BIGINT NOT NULL REFERENCES supported_currencies(id),
  to_currency_id BIGINT NOT NULL REFERENCES supported_currencies(id),
  converted_amount VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS weather (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  city VARCHAR(100),
  region VARCHAR(100),
  temperature DOUBLE,
  feels_like DOUBLE,
  max_temperature DOUBLE,
  min_temperature DOUBLE,
  wind_kph DOUBLE,
  wind_direction VARCHAR(10),
  total_precipitation_mm DOUBLE,
  daily_chance_of_rain INTEGER,
  total_snow_cm DOUBLE,
  daily_chance_of_snow INTEGER,
  humidity INTEGER,
  pressure_mb INTEGER,
  aqi INTEGER,
  is_day BOOLEAN,
  icon VARCHAR(255),
  updated_at DATE
);