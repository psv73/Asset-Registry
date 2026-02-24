
-- Flyway Migration: V2__seed_reference_data.sql
-- Insert initial reference data for statuses, device_types, os, manufacturers.

-- Statuses
INSERT INTO statuses (code, label, note) VALUES
 ('IN_STOCK',   'In stock',   'Asset is available in storage'),
 ('IN_SERVICE', 'In service', 'Asset is under maintenance or repair'),
 ('IN_USE',     'In use',     'Asset is currently assigned'),
 ('RETIRED',    'Retired',    'Asset is decommissioned')
ON CONFLICT (code) DO NOTHING;

-- Device types
INSERT INTO device_types (code, name, note) VALUES
 ('LAPTOP',   'Laptop',   'Portable computer'),
 ('DESKTOP',  'Desktop',  'Stationary computer'),
 ('PRINTER',  'Printer',  'Printing device'),
 ('MONITOR',  'Monitor',  'Display device'),
 ('PHONE',    'Phone',    'Mobile phone or smartphone')
ON CONFLICT (code) DO NOTHING;

-- Manufacturers (examples)
INSERT INTO manufacturers (code, name, note) VALUES
 ('LENOVO', 'Lenovo', 'Common laptop/desktop manufacturer'),
 ('DELL',   'Dell',   'PC and server manufacturer'),
 ('HP',     'HP',     'Computers and printers'),
 ('APPLE',  'Apple',  'Mac, iPhone, iPad, etc.')
ON CONFLICT (code) DO NOTHING;

-- Operating systems (examples)
INSERT INTO os (code, name, version, note) VALUES
 ('WIN10',   'Windows', '10',     'Windows 10 Professional/Enterprise'),
 ('WIN11',   'Windows', '11',     'Windows 11 Professional/Enterprise'),
 ('UBU2204', 'Ubuntu',  '22.04',  'Ubuntu LTS 22.04'),
 ('MACOS14', 'macOS',   '14',     'macOS Sonoma')
ON CONFLICT (code) DO NOTHING;
