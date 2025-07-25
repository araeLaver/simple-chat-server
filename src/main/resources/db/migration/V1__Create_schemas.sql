-- Schema creation for multi-environment setup
-- This script should be run manually by DBA or during initial setup

-- Create development schema
CREATE SCHEMA IF NOT EXISTS chatapp_dev;

-- Create production schema  
CREATE SCHEMA IF NOT EXISTS chatapp_prod;

-- Grant permissions to application user
GRANT ALL PRIVILEGES ON SCHEMA chatapp_dev TO "koyeb-adm";
GRANT ALL PRIVILEGES ON SCHEMA chatapp_prod TO "koyeb-adm";

-- Set default schema search path for development
-- ALTER USER "koyeb-adm" SET search_path TO chatapp_dev, public;

-- Comment for schema purpose
COMMENT ON SCHEMA chatapp_dev IS 'Development environment schema for ChatApp';
COMMENT ON SCHEMA chatapp_prod IS 'Production environment schema for ChatApp';