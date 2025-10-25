
INSERT INTO invoice_sync.side_nav(id, label, icon, route, parent_id)
VALUES
  (1, 'Domov', 'home', '/home', NULL),
  (2, 'Dokumenty', 'document', NULL, NULL),
  (3, 'Faktúry', NULL, '/invoices', 2),
  (4, 'Vystaviť faktúru', NULL, '/invoice/new', 2),
  (5, 'Bločky', NULL, '/web/receipts', 2),
  (6,'XML Convertor','covertrus','/xml-convertor', NULL),
  (7,'Data Transfer','data_trasnfers','/web/data-transfer', NULL),
  (8,'Subscription','subscribtion','/web/limiter', NULL)

