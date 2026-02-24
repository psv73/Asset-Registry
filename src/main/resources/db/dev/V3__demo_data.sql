
-- Flyway Migration: V3__demo_data.sql
-- Demo data for testing purposes only (NOT for production).

-- Insert demo client
INSERT INTO clients (name, email, note)
VALUES ('DemoCorp', 'info@democorp.test', 'Demo client for testing')
ON CONFLICT (name) DO NOTHING;

-- Insert demo location
INSERT INTO locations (name, address, note)
VALUES ('HQ Bratislava', 'Main Street 123, Bratislava, Slovakia', 'Headquarters demo location')
ON CONFLICT (name) DO NOTHING;

-- Get IDs
WITH c AS (SELECT id AS client_id FROM clients WHERE name='DemoCorp'),
     l AS (SELECT id AS location_id FROM locations WHERE name='HQ Bratislava')
INSERT INTO offices (client_id, location_id, room_number, note)
SELECT c.client_id, l.location_id, '101', 'Main office room'
FROM c, l
ON CONFLICT DO NOTHING;

-- Demo employees
WITH c AS (SELECT id AS client_id FROM clients WHERE name='DemoCorp')
INSERT INTO employees (client_id, first_name, last_name, email, phone, note)
VALUES
 ((SELECT client_id FROM c), 'Ivan', 'Ivanov', 'ivan.ivanov@democorp.test', '+421900111222', 'IT Specialist'),
 ((SELECT client_id FROM c), 'Peter', 'Novak', 'peter.novak@democorp.test', '+421900333444', 'Office Manager')
ON CONFLICT (email) DO NOTHING;

-- Demo models (link manufacturer + type)
WITH man AS (SELECT id FROM manufacturers WHERE code='LENOVO'),
     typ AS (SELECT id FROM device_types WHERE code='LAPTOP')
INSERT INTO models (manufacturer_id, device_type_id, name, note)
SELECT man.id, typ.id, 'ThinkPad T480', 'Demo laptop model'
FROM man, typ
ON CONFLICT DO NOTHING;

WITH man AS (SELECT id FROM manufacturers WHERE code='HP'),
     typ AS (SELECT id FROM device_types WHERE code='PRINTER')
INSERT INTO models (manufacturer_id, device_type_id, name, note)
SELECT man.id, typ.id, 'LaserJet Pro M404', 'Demo printer model'
FROM man, typ
ON CONFLICT DO NOTHING;

-- Demo assets
WITH c AS (SELECT id AS client_id FROM clients WHERE name='DemoCorp'),
     m1 AS (SELECT id FROM models WHERE name='ThinkPad T480'),
     m2 AS (SELECT id FROM models WHERE name='LaserJet Pro M404'),
     st_in_stock AS (SELECT id FROM statuses WHERE code='IN_STOCK'),
     st_in_use    AS (SELECT id FROM statuses WHERE code='IN_USE'),
     os_win10     AS (SELECT id FROM os WHERE code='WIN10')
INSERT INTO assets (client_id, model_id, status_id, os_id, inventory_code, serial_number, hostname, dhcp, purchase_date, note)
VALUES
 ((SELECT client_id FROM c), (SELECT id FROM m1), (SELECT id FROM st_in_use), (SELECT id FROM os_win10),
  'INV-1001', 'SN-LENOVO-123', 'demo-laptop-1', FALSE, '2023-05-10', 'Assigned demo laptop'),
 ((SELECT client_id FROM c), (SELECT id FROM m2), (SELECT id FROM st_in_stock), (SELECT id FROM os_win10),
  'INV-2001', 'SN-HP-456', 'demo-printer-1', TRUE, '2022-11-15', 'Demo printer in stock')
ON CONFLICT (inventory_code) DO NOTHING;

-- Demo movements
WITH a1 AS (SELECT id AS asset_id FROM assets WHERE inventory_code='INV-1001'),
     e1 AS (SELECT id AS employee_id FROM employees WHERE email='ivan.ivanov@democorp.test'),
     o1 AS (SELECT id AS office_id FROM offices WHERE room_number='101')
INSERT INTO asset_movements (asset_id, employee_id, office_id, movement_type, effective_from, note)
SELECT a1.asset_id, e1.employee_id, o1.office_id, 'ASSIGN', CURRENT_DATE, 'Assigned demo laptop to Ivan'
FROM a1, e1, o1
ON CONFLICT DO NOTHING;

WITH a2 AS (SELECT id AS asset_id FROM assets WHERE inventory_code='INV-2001'),
     o1 AS (SELECT id AS office_id FROM offices WHERE room_number='101')
INSERT INTO asset_movements (asset_id, office_id, movement_type, effective_from, note)
SELECT a2.asset_id, o1.office_id, 'ASSIGN', CURRENT_DATE, 'Printer placed in office 101'
FROM a2, o1
ON CONFLICT DO NOTHING;
