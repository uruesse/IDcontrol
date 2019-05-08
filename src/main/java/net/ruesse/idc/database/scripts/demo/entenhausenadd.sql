/*
** DEMO-Daten
** Nur zu Testzwecken
**
**/


DELETE FROM IDCREMOTE.ADDRESS WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.CV WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.CONTACT WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.BEITRAG WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.OFFENERECHNUNGEN WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.PERSON WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.PERSON WHERE MGLNR > 1122333700000;
DELETE FROM IDCREMOTE.FAMILY WHERE FNR = 999;

INSERT INTO IDCREMOTE.FAMILY (FNR) VALUES 
(999);

INSERT INTO IDCREMOTE.PERSON (MGLNR,FNR,FIRMA,ANREDE,TITEL,NACHNAME,VORNAME,HAUPTKATEGORIE,GEBURTSDATUM,BEMERKUNG,STATUS,EINTRITT,AUSTRITT,KUENDIGUNG,ABWEICHENDERZAHLER,ZAHLUNGSMODUS) VALUES 
(1122333700001,null,null,'Herr',null,'Duck','Dagobert','Mitglied','1950-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700002,null,null,'Frau',null,'Duck','Daisy','Mitglied','1960-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700003,999,null,'Herr',null,'Duck','Donald','Mitglied','1960-11-05','Frag den Donald mal, warum er nie bezahlt','Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700004,999,null,'Herr',null,'Duck','Tick','Mitglied','2004-11-05','Bitte Kopie Führungszeugnis einsammeln.','Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700005,999,null,'Herr',null,'Duck','Trick','Mitglied','2004-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700006,999,null,'Herr',null,'Duck','Track','Mitglied','2004-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700007,null,null,'Herr',null,'Gans','Gustav','Ex Mitglied','1955-11-05',null,'Gekündigt','1970-01-01','2018-12-31','2018-12-31',false,'Lastschrift');

INSERT INTO IDCREMOTE.ADDRESS (MGLNR,STRASSE,PLZ,ORT,LAND) VALUES 
(1122333700001,'Entenhausener Landweg 1','1234','Entenhausen','USA'),
(1122333700002,'Entenhausener Landweg 2','1234','Entenhausen','USA'),
(1122333700003,'Entenhausener Landweg 3','1234','Entenhausen','USA'),
(1122333700004,'Entenhausener Landweg 3','1234','Entenhausen','USA'),
(1122333700005,'Entenhausener Landweg 3','1234','Entenhausen','USA'),
(1122333700006,'Entenhausener Landweg 3','1234','Entenhausen','USA'),
(1122333700007,'Entenhausener Landweg 5','1234','Entenhausen','USA');

INSERT INTO IDCREMOTE.CV (MGLNR,CVKEY,CVVALUE,VALIDFROM,VALIDTO) VALUES 
(1122333700004,'Funktionen','Ausbilder','2014-11-05',null),
(1122333700006,'Funktionen','Wachgänger','2017-11-05',null);

INSERT INTO IDCREMOTE.BEITRAG (MGLNR,FNR,BEITRAGSPOSITION,BEITRAGSKOMMENTAR,FAELLIG_START,FAELLIG_ENDE,ABRECHNUNGSSTATUS,ZAHLUNGSWEISE) VALUES 
(1122333700002,null,'Wassergeld',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700003,null,'Wassergeld',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700005,null,'Wassergeld',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700007,null,'Wassergeld',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700001,null,'Beitrag Erwachsene',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700002,null,'Beitrag Erwachsene',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700003,null,'Beitrag Erwachsene',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700004,null,'Beitrag Jugendliche',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700005,null,'Beitrag Jugendliche',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700006,null,'Beitrag Jugendliche',null,'2020-01-01','2099-01-01','offen','jährlich'),
(1122333700007,null,'Beitrag Erwachsene',null,'2020-01-01','2099-01-01','offen','jährlich');

INSERT INTO IDCREMOTE.CONTACT (MGLNR,CONTACTKEY,CONTACTVALUE) VALUES 
(1122333700001,'e-Mail','dagobert.duck@duck.com');

INSERT INTO IDCREMOTE.OFFENERECHNUNGEN (RECHNUNGSNUMMER, MGLNR,RECHNUNGSDATUM,ZAHLUNGSZIEL,ZAHLMODUS,MAHNSTUFE,RECHNUNGSSUMME) VALUES 
(9000000001,1122333700002,'2019-02-21','2019-03-08','Bar/Überweisung',0,2250),
(9000000002,1122333700003,'2019-02-21','2019-03-08','Lastschrift',0,5500),
(9000000003,1122333700003,'2019-02-21','2019-03-08','Lastschrift',0,2250);
