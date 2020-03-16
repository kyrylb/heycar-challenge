DROP TABLE IF EXISTS listings;

CREATE TABLE listings (
  id IDENTITY PRIMARY KEY,
  dealer_id INT NOT NULL,
  code VARCHAR(45) NOT NULL,
  make VARCHAR(35) NOT NULL,
  model VARCHAR(25) NOT NULL,
  power_in_ps INT NOT NULL,
  kw INT NOT NULL,
  year_manufactured YEAR NOT NULL,
  color VARCHAR(25) NOT NULL,
  price INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_dealer_id_code ON listings(dealer_id, code);