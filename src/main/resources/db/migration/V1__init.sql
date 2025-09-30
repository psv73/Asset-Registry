
-- Flyway Migration: V1__init.sql
-- PostgreSQL dialect

-- 1) Reference tables without dependencies
CREATE TABLE manufacturers (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_manufacturers_name UNIQUE (name)
);

CREATE TABLE device_types (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_device_types_name UNIQUE (name)
);

CREATE TABLE locations (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200) NOT NULL,
    address          VARCHAR(400),
    note             TEXT,
    CONSTRAINT ux_locations_name UNIQUE (name)
);

CREATE TABLE clients (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200) NOT NULL,
    email            VARCHAR(254),
    note             TEXT,
    CONSTRAINT ux_clients_name UNIQUE (name),
    CONSTRAINT ux_clients_email UNIQUE (email)
);

-- 2) Dependent reference tables
CREATE TABLE models (
    id               BIGSERIAL PRIMARY KEY,
    manufacturer_id  BIGINT NOT NULL REFERENCES manufacturers(id),
    device_type_id   BIGINT NOT NULL REFERENCES device_types(id),
    name             VARCHAR(200) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_models_manufacturer_name UNIQUE (manufacturer_id, name)
);

CREATE TABLE offices (
    id               BIGSERIAL PRIMARY KEY,
    location_id      BIGINT NOT NULL REFERENCES locations(id),
    room_number      VARCHAR(50) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_offices_location_room UNIQUE (location_id, room_number)
);

CREATE TABLE employees (
    id               BIGSERIAL PRIMARY KEY,
    client_id        BIGINT REFERENCES clients(id),
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(254),
    phone            VARCHAR(50),
    note             TEXT,
    CONSTRAINT ux_employees_email UNIQUE (email)
);

CREATE TABLE products (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200) NOT NULL,
    vendor           VARCHAR(200) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_products_name_vendor UNIQUE (name, vendor)
);

CREATE TABLE os (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(120) NOT NULL,
    version          VARCHAR(120) NOT NULL,
    note             TEXT,
    CONSTRAINT ux_os_name_version UNIQUE (name, version)
);

-- 3) Core table: assets
CREATE TABLE assets (
    id               BIGSERIAL PRIMARY KEY,
    model_id         BIGINT      NOT NULL REFERENCES models(id),
    is_retired       BOOLEAN     NOT NULL DEFAULT FALSE,
    inventory_code   VARCHAR(100) UNIQUE,
    serial_number    VARCHAR(150) UNIQUE,
    hostname         VARCHAR(255),
    ip               INET,
    dhcp             BOOLEAN     NOT NULL DEFAULT TRUE,
    purchase_date    DATE,
    params           JSONB,
    note             TEXT,
    create_datetime  TIMESTAMP   NOT NULL DEFAULT NOW(),
    update_datetime  TIMESTAMP,
    CONSTRAINT ck_assets_static_ip CHECK (dhcp OR ip IS NOT NULL)
);

-- Partial unique index for static IPs
CREATE UNIQUE INDEX ux_assets_ip_static
    ON assets(ip)
    WHERE dhcp = FALSE AND ip IS NOT NULL;

-- JSONB GIN index for flexible querying of params
CREATE INDEX ix_assets_params_gin ON assets USING GIN (params);

-- 4) Licenses (depends on products)
CREATE TABLE licenses (
    id                 BIGSERIAL PRIMARY KEY,
    product_id         BIGINT NOT NULL REFERENCES products(id),
    license_key        VARCHAR(255) NOT NULL,
    license_expires_on DATE,
    account            VARCHAR(255),
    note               TEXT,
    CONSTRAINT ux_licenses_license_key UNIQUE (license_key)
);

-- 5) Asset ↔ Software junction
-- Note: to allow NULLable license_id/product_id with uniqueness, we use a surrogate PK
--       and two partial unique indexes (not a composite PK with NULLs).
CREATE TABLE asset_software (
    id             BIGSERIAL PRIMARY KEY,
    asset_id       BIGINT NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    license_id     BIGINT REFERENCES licenses(id),
    product_id     BIGINT REFERENCES products(id),
    installed_at   DATE   NOT NULL DEFAULT CURRENT_DATE,
    uninstalled_at DATE,
    note           TEXT,
    CONSTRAINT ck_asset_software_refs CHECK (license_id IS NOT NULL OR product_id IS NOT NULL),
    CONSTRAINT ck_asset_software_period CHECK (uninstalled_at IS NULL OR uninstalled_at >= installed_at)
);

-- Ensure one license cannot be assigned twice to the same asset at the same time
CREATE UNIQUE INDEX ux_asset_software_asset_license
    ON asset_software(asset_id, license_id)
    WHERE license_id IS NOT NULL AND uninstalled_at IS NULL;

-- Ensure the same free product is not duplicated on the same asset (for active installations)
CREATE UNIQUE INDEX ux_asset_software_asset_product
    ON asset_software(asset_id, product_id)
    WHERE product_id IS NOT NULL AND uninstalled_at IS NULL;

-- 6) Asset movements (depends on assets, employees, offices)
CREATE TABLE asset_movements (
    id             BIGSERIAL PRIMARY KEY,
    asset_id       BIGINT NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    employee_id    BIGINT NOT NULL REFERENCES employees(id),
    office_id      BIGINT NOT NULL REFERENCES offices(id),
    movement_type  VARCHAR(30) NOT NULL, -- e.g. assign, transfer, repair, retire
    effective_from DATE NOT NULL,
    effective_to   DATE,
    recorded_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    note           TEXT,
    CONSTRAINT ck_asset_movements_period CHECK (effective_to IS NULL OR effective_to >= effective_from)
);

-- Optional: One active movement per asset (enforced for rows with no effective_to)
CREATE UNIQUE INDEX ux_asset_movements_active
    ON asset_movements(asset_id)
    WHERE effective_to IS NULL;
