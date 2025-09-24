-- V3: add clients and scope assets/employees by client

-- 1) Clients
CREATE TABLE IF NOT EXISTS clients (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    note TEXT
);

-- 2) Add client_id to employees and assets
ALTER TABLE employees ADD COLUMN IF NOT EXISTS client_id BIGINT REFERENCES clients(id);
ALTER TABLE assets    ADD COLUMN IF NOT EXISTS client_id BIGINT REFERENCES clients(id);

-- 3) Drop old unique constraints on assets (if existed) and create scoped unique indexes
DO $$
BEGIN
  BEGIN
    ALTER TABLE assets DROP CONSTRAINT uk_assets_inventory_code;
  EXCEPTION WHEN undefined_object THEN
  END;
  BEGIN
    ALTER TABLE assets DROP CONSTRAINT uk_assets_serial_number;
  EXCEPTION WHEN undefined_object THEN
  END;
END $$;

-- 4) Create scoped unique indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_assets_client_inventory
  ON assets(client_id, inventory_code) WHERE inventory_code IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_assets_client_serial
  ON assets(client_id, serial_number) WHERE serial_number IS NOT NULL;

-- 5) Seed default client and backfill
INSERT INTO clients(name, note)
    VALUES ('Default Client', 'auto-created by V3 migration')
ON CONFLICT (name) DO NOTHING;

UPDATE employees SET client_id = (SELECT id FROM clients WHERE name = 'Default Client') WHERE client_id IS NULL;
UPDATE assets    SET client_id = (SELECT id FROM clients WHERE name = 'Default Client') WHERE client_id IS NULL;

-- 6) Not null
ALTER TABLE employees ALTER COLUMN client_id SET NOT NULL;
ALTER TABLE assets    ALTER COLUMN client_id SET NOT NULL;

-- 7) Indexes
CREATE INDEX IF NOT EXISTS idx_assets_client    ON assets(client_id);
CREATE INDEX IF NOT EXISTS idx_employees_client ON employees(client_id);
