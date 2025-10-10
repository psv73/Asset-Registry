-- Initialize application role and grant ownership/privileges.
-- This script runs once on first container init.
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'asset') THEN
    CREATE ROLE asset LOGIN PASSWORD 'asset';
  END IF;
END
$$;

-- Owner is the app role
ALTER DATABASE asset_registry OWNER TO asset;

-- Work inside the default schema
ALTER SCHEMA public OWNER TO asset;
GRANT ALL ON SCHEMA public TO asset;

-- App role should have full privileges on DB
GRANT ALL PRIVILEGES ON DATABASE asset_registry TO asset;
