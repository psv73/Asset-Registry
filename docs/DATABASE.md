## Table: assets
**Purpose:** Stores information about company assets (hardware, devices).

### Columns
- `id` — Primary key.
- `client_id` — FK → `clients.id`, NOT NULL. Owner company/client.
- `model_id` — FK → `models.id`, NOT NULL.
- `status_id` — FK → `statuses.id`, NOT NULL. (e.g., In stock / In service / In use / Retired)
- `os_id` — FK → `os.id`, NOT NULL.
- `inventory_code` — varchar, UNIQUE. Internal inventory number.
- `serial_number` — varchar, UNIQUE. Manufacturer’s serial number. *(Remove uniqueness if duplicates are possible.)*
- `hostname` — varchar. Network hostname, optional.
- `ip` — inet. Device IP address, NULL if `dhcp = true`.
- `dhcp` — boolean, NOT NULL, default true. Indicates if the device uses dynamic IP.
- `purchase_date` — date. Asset purchase date.
- `params` — jsonb. Additional parameters or configuration details.
- `note` — text. Free-text notes.
- `create_datetime` — timestamp, NOT NULL, default now().
- `update_datetime` — timestamp. Last update timestamp.

### Relations
- `client_id` → `clients.id`
- `model_id` → `models.id`
- `status_id` → `statuses.id`
- `os_id` → `os.id`

### Constraints
- UNIQUE(inventory_code)
- UNIQUE(serial_number) *(optional, depending on business rules)*
- CHECK (dhcp OR ip IS NOT NULL) — ensures that static-IP assets have a valid IP.
- UNIQUE(ip) WHERE dhcp = false — prevents duplicate static IP addresses.

## Table: models
**Purpose:** Catalog of device models tied to a manufacturer and a device type.

### Columns
- `id` — Primary key.
- `manufacturer_id` — FK → `manufacturers.id`, NOT NULL.
- `device_type_id` — FK → `device_types.id`, NOT NULL.
- `name` — varchar, NOT NULL. Human-readable model name (e.g., "ThinkPad T480").
- `note` — text. Optional notes (e.g., common configs).

### Relations
- `manufacturer_id` → `manufacturers.id`
- `device_type_id` → `device_types.id`

### Constraints
- UNIQUE(manufacturer_id, name) — prevents duplicate model names per manufacturer.

## Table: asset_movements
**Purpose:** Tracks the assignment and movement of assets between employees and offices.

### Columns
- `id` — Primary key.
- `asset_id` — FK → `assets.id`, NOT NULL.
- `employee_id` — FK → `employees.id`, optional. Employee using the asset (NULL if asset is in stock, in service, or retired).
- `office_id` — FK → `offices.id`, NOT NULL. Office/room where the asset is located.
- `movement_type` — enum, NOT NULL. One of: `ASSIGN`, `TRANSFER`, `REPAIR`, `RETIRE`.
- `effective_from` — date, NOT NULL. Date when the movement becomes effective.
- `effective_to` — date, optional. Date when the movement ends (NULL for active assignments).
- `recorded_at` — timestamp, NOT NULL DEFAULT now(). Date/time when the record was created.
- `note` — text. Optional notes.

### Relations
- `asset_id` → `assets.id`
- `employee_id` → `employees.id` (nullable)
- `office_id` → `offices.id`

### Constraints
- CHECK (movement_type IN ('ASSIGN','TRANSFER','REPAIR','RETIRE')) — ensures only valid codes are used.
- CHECK (`effective_to IS NULL OR effective_to >= effective_from`) — ensures valid date ranges.
- UNIQUE(asset_id) WHERE `effective_to IS NULL` — ensures only one active movement per asset.

## Table: employees
**Purpose:** Stores information about employees who are assigned assets.

### Columns
- `id` — Primary key.
- `client_id` — FK → `clients.id`, NOT NULL. The client/company the employee belongs to.
- `first_name` — varchar, NOT NULL. Employee’s first name.
- `last_name` — varchar, NOT NULL. Employee’s last name.
- `email` — varchar, UNIQUE. Employee’s email address.
- `phone` — varchar. Employee’s phone number.
- `note` — text. Optional notes (e.g., department, position).

### Relations
- `client_id` → `clients.id`

### Constraints
- UNIQUE(email) — prevents duplicate email addresses.

## Table: clients
**Purpose:** Stores information about clients (companies or organizations) related to employees or assets.

### Columns
- `id` — Primary key.
- `name` — varchar, NOT NULL. Client’s name.
- `email` — varchar, UNIQUE. Contact email.
- `note` — text. Optional notes (e.g., additional contacts, description).

### Relations
*(none, referenced by `employees`)*

### Constraints
- UNIQUE(name) — prevents duplicate client names.
- UNIQUE(email) — ensures contact email is unique.

## Table: offices
**Purpose:** Stores offices/rooms belonging to a specific client and located at a specific location.

### Columns
- `id` — Primary key.
- `client_id` — FK → `clients.id`, NOT NULL. Owner client.
- `location_id` — FK → `locations.id`, NOT NULL. Physical site/building.
- `room_number` — varchar, NOT NULL. Room/office identifier within the location.
- `note` — text. Optional notes.

### Relations
- `client_id` → `clients.id`
- `location_id` → `locations.id`

### Constraints
- UNIQUE(client_id, location_id, room_number) — prevents duplicate rooms for the same client and location.

## Table: locations
**Purpose:** Stores information about physical locations (e.g., buildings, sites, cities).

### Columns
- `id` — Primary key.
- `name` — varchar, NOT NULL. Location name (e.g., "Bratislava HQ").
- `address` — varchar. Full address of the location.
- `note` — text. Optional notes.

### Relations
*(none, referenced by `offices`)*

### Constraints
- UNIQUE(name) — prevents duplicate location names.

## Table: asset_software
**Purpose:** Junction table linking assets with installed software.  
Stores either a license reference (if the product requires a license) or a direct product reference (for free/trial software).

### Columns
- `id` — Primary key.
- `asset_id` — FK → `assets.id`, NOT NULL.
- `license_id` — FK → `licenses.id`, optional. Specifies which license key is used on the asset.
- `product_id` — FK → `products.id`, optional. Used only when the product does not require a license (e.g., free or trial software).
- `installed_at` — date, NOT NULL DEFAULT current_date. Date of installation.
- `uninstalled_at` — date, optional. Date of removal (nullable if still installed).
- `note` — text. Optional notes.

### Relations
- `asset_id` → `assets.id`
- `license_id` → `licenses.id`
- `product_id` → `products.id`

### Constraints
- CHECK (license_id IS NOT NULL OR product_id IS NOT NULL) — ensures that at least one reference is set.
- CHECK (uninstalled_at IS NULL OR uninstalled_at >= installed_at) — ensures valid installation periods.
- UNIQUE(asset_id, license_id) WHERE license_id IS NOT NULL AND uninstalled_at IS NULL — prevents assigning the same license twice to the same asset at the same time.
- UNIQUE(asset_id, product_id) WHERE product_id IS NOT NULL AND uninstalled_at IS NULL — prevents duplicate free/trial product installations on the same asset.

## Table: products
**Purpose:** Catalog of software products.

### Columns
- `id` — Primary key.
- `name` — varchar, NOT NULL. Product name (e.g., "Microsoft Office").
- `vendor` — varchar, NOT NULL. Vendor or producer of the product (e.g., "Microsoft").
- `note` — text. Optional notes.

### Relations
*(referenced by `licenses` and `asset_software`)*

### Constraints
- UNIQUE(name, vendor) — prevents duplicate product entries.

## Table: licenses
**Purpose:** Stores license keys for software products.

### Columns
- `id` — Primary key.
- `product_id` — FK → `products.id`, NOT NULL. Product this license belongs to.
- `license_key` — varchar, NOT NULL. Unique license identifier/key.
- `license_expires_on` — date. Optional date. Expiration date (NULL if perpetual).
- `account` — varchar. Optional account (NULL if not set). Account/email the license is registered to.
- `note` — text. Optional notes.

### Relations
- `product_id` → `products.id`

### Constraints
- UNIQUE(license_key) — prevents duplicate keys.

## Table: os
**Purpose:** Reference catalog of operating systems.

### Columns
- `id` — Primary key.
- `code` — varchar, NOT NULL. Machine-readable code (e.g., `WIN10`, `UBU2204`, `MACOS14`).
- `name` — varchar, NOT NULL. OS family or name (e.g., "Windows", "Ubuntu", "macOS").
- `version` — varchar, NOT NULL. Version or release (e.g., "10", "22.04", "14").
- `note` — text. Optional notes (e.g., edition, build, architecture).

### Relations
*(none, referenced by `assets`)*

### Constraints
- UNIQUE(code)
- UNIQUE(name, version)

## Table: manufacturers
**Purpose:** Reference catalog of hardware manufacturers.

### Columns
- `id` — Primary key.
- `code` — varchar, NOT NULL. Machine-readable code (e.g., `LENOVO`, `DELL`, `HP`).
- `name` — varchar, NOT NULL. Manufacturer name (e.g., "Lenovo", "Dell", "HP").
- `note` — text. Optional notes.

### Relations
*(none, referenced by `models`)*

### Constraints
- UNIQUE(code)
- UNIQUE(name)

## Table: device_types
**Purpose:** Reference catalog of device categories (e.g., Laptop, Printer, Monitor).

### Columns
- `id` — Primary key.
- `code` — varchar, NOT NULL. Machine-readable code (e.g., `LAPTOP`, `PRINTER`, `MONITOR`).
- `name` — varchar, NOT NULL. Human-readable name (e.g., "Laptop", "Printer").
- `note` — text. Optional notes.

### Relations
*(none, referenced by `models`)*

### Constraints
- UNIQUE(code)
- UNIQUE(name)

## Table: statuses
**Purpose:** Reference table of asset lifecycle statuses.

### Columns
- `id` — Primary key.
- `code` — varchar, NOT NULL. Machine-readable code (e.g., `IN_STOCK`, `IN_SERVICE`, `IN_USE`, `RETIRED`).
- `label` — varchar, NOT NULL. Human-readable label (e.g., "In stock", "In service", "In use", "Retired").
- `note` — text. Optional notes.

### Relations
*(none, referenced by `assets`)*

### Constraints
- UNIQUE(code)
- UNIQUE(label)
- CHECK (code IN ('IN_STOCK','IN_SERVICE','IN_USE','RETIRED'))
