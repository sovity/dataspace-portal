alter table component alter column organization_id drop not null;
alter table component alter column created_by drop not null;

delete from component_downtimes;
delete from component;
delete from connector_downtimes;
delete from data_offer_view_count;
delete from data_offer;
delete from contract_offer;
delete from crawler_event_log;
delete from crawler_execution_time_measurement;
delete from connector;

alter table data_offer
  drop column data_category,
  drop column data_subcategory,
  drop column transport_mode,
  drop column geo_reference_method;
