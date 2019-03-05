/*
** DEMO-Daten
** Nur zu Testzwecken
**
**/

DELETE FROM OPENPOST WHERE MGLNR > 1122333700000;
DELETE FROM ADDRESS WHERE MGLNR > 1122333700000;
DELETE FROM CV WHERE MGLNR > 1122333700000;
DELETE FROM CONTACT WHERE MGLNR > 1122333700000;
DELETE FROM PERSON WHERE MGLNR > 1122333700000;

INSERT INTO PERSON (MGLNR,FNR,FIRMA,ANREDE,TITEL,NACHNAME,VORNAME,HAUPTKATEGORIE,GEBURTSDATUM,BEMERKUNG,STATUS,EINTRITT,AUSTRITT,KUENDIGUNG,ABWEICHENDERZAHLER,ZAHLUNGSMODUS) VALUES 
(1122333700001,null,null,'Herr',null,'Duck','Dagobert','Mitglied','1950-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700002,null,null,'Frau',null,'Duck','Daisy','Mitglied','1960-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700003,null,null,'Herr',null,'Duck','Donald','Mitglied','1960-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700004,null,null,'Herr',null,'Duck','Tick','Mitglied','2004-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700005,null,null,'Herr',null,'Duck','Trick','Mitglied','2004-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700006,null,null,'Herr',null,'Duck','Track','Mitglied','2004-11-05',null,'Aktiv','1970-01-01',null,null,false,'Lastschrift'),
(1122333700007,null,null,'Herr',null,'Gans','Gustav','Mitglied','1955-11-05',null,'Aktiv','1970-01-01','2018-12-31','2018-12-31',false,'Lastschrift');

INSERT INTO ADDRESS (MGLNR,STRASSE,PLZ,ORT,LAND) VALUES 
(1122333700001,'Entenhausener Landweg 1','1234','Entenhausen','USA'),
(1122333700002,'Entenhausener Landweg 2','1234','Entenhausen','USA'),
(1122333700003,'Entenhausener Landweg 3','1234','Entenhausen','USA'),
(1122333700004,'Entenhausener Landweg 2','1234','Entenhausen','USA'),
(1122333700005,'Entenhausener Landweg 2','1234','Entenhausen','USA'),
(1122333700006,'Entenhausener Landweg 2','1234','Entenhausen','USA'),
(1122333700007,'Entenhausener Landweg 5','1234','Entenhausen','USA');

INSERT INTO CV (MGLNR,CVKEY,CVVALUE,VALIDFROM,VALIDTO) VALUES 
(1122333700004,'Funktionen','Ausbilder','2014-11-05',null),
(1122333700006,'Funktionen','Wachgänger','2017-11-05',null);

INSERT INTO CONTACT (MGLNR,CONTACTKEY,CONTACTVALUE) VALUES 
(1122333700001,'e-Mail','dagobert.duck@duck.com');

INSERT INTO OPENPOST (MGLNR,SUMMEVORJAHR,SUMMEAKTJAHR) VALUES 
(1122333700001,0,0),
(1122333700002,0,0),
(1122333700003,5500,6750),
(1122333700004,0,0),
(1122333700005,0,0),
(1122333700006,0,0),
(1122333700007,0,0);