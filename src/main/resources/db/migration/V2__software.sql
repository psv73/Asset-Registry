-- ===== ПО =====
CREATE TABLE software_products (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(255) NOT NULL UNIQUE,
    vendor VARCHAR(255)
);
CREATE TABLE asset_software (
    id                  BIGSERIAL PRIMARY KEY,
    asset_id            BIGINT NOT NULL REFERENCES assets(id),
    product_id          BIGINT NOT NULL REFERENCES software_products(id),
    version             VARCHAR(255),
    license_key         TEXT,
    license_expires_on  DATE,
    note                TEXT
);
CREATE INDEX idx_asset_software_asset ON asset_software(asset_id);
CREATE INDEX idx_asset_software_product_ver ON asset_software(product_id, version);
CREATE INDEX idx_asset_software_expiry ON asset_software(license_expires_on);
