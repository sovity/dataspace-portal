-- Changes to Enums are non-transactional and must be supplied in a separate migration script for flyway

-- Connector deleted due to being offline for too long
alter type broker_event_type add value 'CONNECTOR_DELETED_DUE_TO_OFFLINE_FOR_TOO_LONG';
