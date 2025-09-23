-- ===== Справочники =====
CREATE TABLE asset_types (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE manufacturers (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE models (
    id              BIGSERIAL PRIMARY KEY,
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id),
    name            VARCHAR(255) NOT NULL,
    mtm             VARCHAR(255),
    UNIQUE (manufacturer_id, name)
);
CREATE TABLE device_types (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE oses (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE locations (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE offices (
    id          BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    label       VARCHAR(255) NOT NULL,
    UNIQUE (location_id, label)
);
CREATE TABLE statuses (
    id    BIGSERIAL PRIMARY KEY,
    code  VARCHAR(64) NOT NULL UNIQUE,
    label VARCHAR(255) NOT NULL
);
CREATE TABLE employees (
    id         BIGSERIAL PRIMARY KEY,
    last_name  VARCHAR(255),
    first_name VARCHAR(255),
    UNIQUE (last_name, first_name)
);

-- ===== Активы (железо) =====
CREATE TABLE assets (
    id               BIGSERIAL PRIMARY KEY,
    asset_type_id    BIGINT REFERENCES asset_types(id),
    manufacturer_id  BIGINT REFERENCES manufacturers(id),
    model_id         BIGINT REFERENCES models(id),
    device_type_id   BIGINT REFERENCES device_types(id),
    os_id            BIGINT REFERENCES oses(id),

    inventory_code   VARCHAR(255) UNIQUE,
    serial_number    VARCHAR(255) UNIQUE,
    delivery_date    DATE,
    ip_address       VARCHAR(64),

    params           TEXT,
    note             TEXT,

    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ===== История перемещений =====
CREATE TABLE asset_movements (
    id            BIGSERIAL PRIMARY KEY,
    asset_id      BIGINT NOT NULL REFERENCES assets(id),
    employee_id   BIGINT REFERENCES employees(id),
    office_id     BIGINT REFERENCES offices(id),
    status_id     BIGINT REFERENCES statuses(id),

    effective_from DATE NOT NULL,
    recorded_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    note          TEXT
);

CREATE INDEX idx_assets_serial ON assets(serial_number);
CREATE INDEX idx_movements_asset ON asset_movements(asset_id);
