-- V1__init.sql
-- Initial schema based on DATABASE.md

-- === Base reference tables (no FKs to others) ===

CREATE TABLE IF NOT EXISTS clients (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255),
    note    TEXT,
    CONSTRAINT uq_clients_name  UNIQUE (name),
    CONSTRAINT uq_clients_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS locations (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    note    TEXT,
    CONSTRAINT uq_locations_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS device_types (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(64)  NOT NULL,
    name    VARCHAR(128) NOT NULL,
    note    TEXT,
    CONSTRAINT uq_device_types_code UNIQUE (code),
    CONSTRAINT uq_device_types_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS manufacturers (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(64)  NOT NULL,
    name    VARCHAR(128) NOT NULL,
    note    TEXT,
    CONSTRAINT uq_manufacturers_code UNIQUE (code),
    CONSTRAINT uq_manufacturers_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS os (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(64)  NOT NULL,
    name    VARCHAR(128) NOT NULL,
    version VARCHAR(64)  NOT NULL,
    note    TEXT,
    CONSTRAINT uq_os_code         UNIQUE (code),
    CONSTRAINT uq_os_name_version UNIQUE (name, version)
);

CREATE TABLE IF NOT EXISTS statuses (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(64)  NOT NULL,
    label   VARCHAR(128) NOT NULL,
    note    TEXT,
    CONSTRAINT uq_statuses_code  UNIQUE (code),
    CONSTRAINT uq_statuses_label UNIQUE (label),
    CONSTRAINT ck_statuses_code CHECK (code IN ('IN_STOCK','IN_SERVICE','IN_USE','RETIRED'))
);

CREATE TABLE IF NOT EXISTS products (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    vendor  VARCHAR(255) NOT NULL,
    note    TEXT,
    CONSTRAINT uq_products_name_vendor UNIQUE (name, vendor)
);

-- === Dependent references ===

CREATE TABLE IF NOT EXISTS licenses (
    id                 BIGSERIAL PRIMARY KEY,
    product_id         BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    license_key        VARCHAR(255) NOT NULL,
    license_expires_on DATE,
    account            VARCHAR(255),
    note               TEXT,
    CONSTRAINT uq_licenses_license_key UNIQUE (license_key)
);

CREATE TABLE IF NOT EXISTS models (
    id              BIGSERIAL PRIMARY KEY,
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id) ON DELETE RESTRICT,
    device_type_id  BIGINT NOT NULL REFERENCES device_types(id)  ON DELETE RESTRICT,
    name            VARCHAR(128) NOT NULL,
    note            TEXT,
    CONSTRAINT uq_models_manufacturer_name UNIQUE (manufacturer_id, name)
);

CREATE TABLE IF NOT EXISTS employees (
    id         BIGSERIAL PRIMARY KEY,
    client_id  BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    first_name VARCHAR(64)  NOT NULL,
    last_name  VARCHAR(64)  NOT NULL,
    email      VARCHAR(255),
    phone      VARCHAR(64),
    note       TEXT,
    CONSTRAINT uq_employees_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS offices (
    id          BIGSERIAL PRIMARY KEY,
    client_id   BIGINT NOT NULL REFERENCES clients(id)   ON DELETE RESTRICT,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    room_number VARCHAR(128) NOT NULL,
    note        TEXT,
    CONSTRAINT uq_offices_room UNIQUE (client_id, location_id, room_number)
);

-- === Assets ===

CREATE TABLE IF NOT EXISTS assets (
    id               BIGSERIAL PRIMARY KEY,
    client_id        BIGINT NOT NULL REFERENCES clients(id)   ON DELETE RESTRICT,
    model_id         BIGINT NOT NULL REFERENCES models(id)    ON DELETE RESTRICT,
    status_id        BIGINT NOT NULL REFERENCES statuses(id)  ON DELETE RESTRICT,
    os_id            BIGINT NOT NULL REFERENCES os(id)        ON DELETE RESTRICT,

    inventory_code   VARCHAR(128),
    serial_number    VARCHAR(128),

    hostname         VARCHAR(255),
    ip               INET,
    dhcp             BOOLEAN NOT NULL DEFAULT TRUE,

    purchase_date    DATE,
    params           JSONB,
    note             TEXT,

    create_datetime  TIMESTAMP NOT NULL DEFAULT now(),
    update_datetime  TIMESTAMP,

    CONSTRAINT uq_assets_inventory_code UNIQUE (inventory_code),
    -- Serial number uniqueness is optional per business rules; keep enabled by default:
    CONSTRAINT uq_assets_serial_number  UNIQUE (serial_number)
);
/* Partial unique index: prevent duplicate static IPs (dhcp=false) */
CREATE UNIQUE INDEX IF NOT EXISTS uq_assets_ip_static
    ON assets (ip)
    WHERE dhcp = FALSE;

-- === Movements ===

CREATE TABLE IF NOT EXISTS asset_movements (
    id             BIGSERIAL PRIMARY KEY,
    asset_id       BIGINT NOT NULL REFERENCES assets(id)     ON DELETE CASCADE,
    employee_id    BIGINT     REFERENCES employees(id)       ON DELETE SET NULL, -- nullable
    office_id      BIGINT NOT NULL REFERENCES offices(id)    ON DELETE RESTRICT,

    movement_type  VARCHAR(16) NOT NULL,
    effective_from DATE        NOT NULL,
    effective_to   DATE,
    recorded_at    TIMESTAMP   NOT NULL DEFAULT now(),
    note           TEXT,

    CONSTRAINT ck_movement_type CHECK (movement_type IN ('ASSIGN','TRANSFER','REPAIR','RETIRE')),
    CONSTRAINT ck_movement_dates CHECK (effective_to IS NULL OR effective_to >= effective_from)
);
/* Only one active (effective_to IS NULL) movement per asset */
CREATE UNIQUE INDEX IF NOT EXISTS uq_asset_mov_active
    ON asset_movements (asset_id)
    WHERE effective_to IS NULL;

-- === Asset ↔ Software junction ===

CREATE TABLE IF NOT EXISTS asset_software (
    id             BIGSERIAL PRIMARY KEY,
    asset_id       BIGINT NOT NULL REFERENCES assets(id)    ON DELETE CASCADE,
    license_id     BIGINT     REFERENCES licenses(id)       ON DELETE SET NULL,
    product_id     BIGINT     REFERENCES products(id)       ON DELETE SET NULL,
    installed_at   DATE NOT NULL DEFAULT current_date,
    uninstalled_at DATE,
    note           TEXT,

    -- at least one reference present
    CONSTRAINT ck_asset_sw_ref CHECK (license_id IS NOT NULL OR product_id IS NOT NULL),
    CONSTRAINT ck_asset_sw_dates CHECK (uninstalled_at IS NULL OR uninstalled_at >= installed_at)
);
/* Prevent duplicates while active (uninstalled_at IS NULL) */
CREATE UNIQUE INDEX IF NOT EXISTS uq_asset_sw_license_active
    ON asset_software (asset_id, license_id)
    WHERE license_id IS NOT NULL AND uninstalled_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_asset_sw_product_active
    ON asset_software (asset_id, product_id)
    WHERE product_id IS NOT NULL AND uninstalled_at IS NULL;

-- === Helpful indexes (lookups) ===
CREATE INDEX IF NOT EXISTS idx_assets_model       ON assets (model_id);
CREATE INDEX IF NOT EXISTS idx_assets_status      ON assets (status_id);
CREATE INDEX IF NOT EXISTS idx_assets_os          ON assets (os_id);
CREATE INDEX IF NOT EXISTS idx_assets_client      ON assets (client_id);
CREATE INDEX IF NOT EXISTS idx_assets_serial      ON assets (serial_number);
CREATE INDEX IF NOT EXISTS idx_movements_asset    ON asset_movements (asset_id);
CREATE INDEX IF NOT EXISTS idx_movements_office   ON asset_movements (office_id);
CREATE INDEX IF NOT EXISTS idx_movements_fromdate ON asset_movements (effective_from);
