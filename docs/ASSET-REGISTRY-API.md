# Assets API (v1)

Base prefix: `/api/v1`

## Health
- `GET /api/v1/health` → `{"status":"UP"}`

## Assets

### List (filter by inventoryCode)
- `GET /api/v1/assets`
- `GET /api/v1/assets?inventoryCode=INV-001` → `[]` or single-element array

### Get by ID
- `GET /api/v1/assets/{id}` → 200 / 404

### Create
- `POST /api/v1/assets`
- Body (JSON):
```json
{
  "clientId": 1,
  "modelId": 1,
  "statusId": 1,
  "osId": 1,
  "inventoryCode": "INV-001",
  "serialNumber": "SN-001",
  "hostname": "pc-001",
  "ip": "192.168.0.10",
  "dhcp": true,
  "purchaseDate": "2025-10-10",
  "params": {"ram":"16GB","cpu":"i7"},
  "note": "first asset"
}
```

### Update
- `PUT /api/v1/assets/{id}` → 200 / 404

### Delete
- `DELETE /api/v1/assets/{id}` → 204 / 404

## Error format
```json
{
  "timestamp": "2025-10-10T10:00:00+02:00",
  "status": 400,
  "error": "validation_failed",
  "message": "Validation failed",
  "path": "/api/v1/assets",
  "fields": {
    "clientId": "must not be null"
  }
}
```

Codes:
- `400 bad_request / validation_failed`
- `404 not_found`
- `409 duplicate_key` (unique constraint)
- `500 internal_error`

Notes:
- Auth is disabled (dev only).
- `ip` stored as Postgres `INET`, exposed as string.
- `params` is JSONB arbitrary map.
