/*
** DEMO-Daten
** Nur zu Testzwecken
**
**/



DELETE FROM IDCREMOTE.VEREIN;
INSERT INTO IDCREMOTE.VEREIN (
  MGLNR,
  FIRMA,
  STRASSE,
  PLZ,
  ORT,
  LAND,
  REGISTER,
  EMAIL,
  URI,
  DATATIME) 
VALUES (
  1122333,
  'Schwimmverein Entenhausen',
  'Entenhausener Landweg 100',
  '12345',
  'Entenhausen',
  'USA',
  'Amtsgericht Entenhausen 12345',
  'info@entenhausener-sv.us',
  'www.entenhausener-sv.us',
  '2000-01-01 00:00:00.000');