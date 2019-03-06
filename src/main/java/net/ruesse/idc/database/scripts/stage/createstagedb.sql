drop table rechnungen_stage;
drop table beitragspositionen_stage;
drop table lebenslauf_stage;
drop table person_stage;
/*
create table person_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
nummer int,
mitgliedsnummer_sewobe bigint,
familiennummer_sewobe int,
firma varchar(128),
anrede varchar(128),
titel varchar(128),
nachname varchar(128),
vorname varchar(128),
strasse varchar(128),
plz varchar(16),
ort varchar(128),
land varchar(128),
hauptkategorie varchar(128),
telefon_1 varchar(128),
telefon_2 varchar(128),
mobil varchar(128),
telefax varchar(128),
email varchar(128),
geburtsdatum date,
bemerkung varchar(128),
status varchar(128),
eintritt date,
austritt date,
kuendigung date,
iban varchar(128),
bic varchar(128),
kontoinhaber varchar(128),
mandatsreferenz varchar(128),
mandat_vorhanden boolean,
abweichenderzahler boolean,
beitragsposition varchar(128),
beitragskommentar varchar(128),
faellig_start date,
faellig_ende date,
abrechnungsstatus varchar(128),
zahlungsmodus varchar(128),
zahlungsweise varchar(128),
offene_beitraege int,
alter_errechnet int,
rechnungsdatum date,
rechnungsnummer bigint,
rechnungssumme int
);
*/

create table person_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
nummer int,
mitgliedsnummer_sewobe bigint,
familiennummer_sewobe int,
firma varchar(128),
anrede varchar(128),
titel varchar(128),
nachname varchar(128),
vorname varchar(128),
strasse varchar(128),
plz varchar(16),
ort varchar(128),
land varchar(128),
hauptkategorie varchar(128),
telefon_1 varchar(128),
telefon_2 varchar(128),
mobil varchar(128),
telefax varchar(128),
email varchar(128),
geburtsdatum date,
bemerkung varchar(128),
status varchar(128),
eintritt date,
austritt date,
kuendigung date,
iban varchar(128),
bic varchar(128),
kontoinhaber varchar(128),
mandatsreferenz varchar(128),
mandat_vorhanden boolean,
zahlungsmodus varchar(128),
abweichenderzahler boolean
);

create table rechnungen_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
mitgliedsnummer_sewobe bigint,
familiennummer_sewobe int,
rechnungsdatum date,
rechnungsnummer bigint,
rechnungssumme int,
ZAHLMODUS varchar(128),
ZAHLUNGSZIEL date
);

create table beitragspositionen_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
mitgliedsnummer_sewobe bigint,
familiennummer_sewobe int,
BEITRAGSPOSITION varchar(128),
BEITRAGSKOMMENTAR varchar(128),
FAELLIG_START date,
FAELLIG_ENDE date,
ABRECHNUNGSSTATUS varchar(128),
ZAHLUNGSWEISE varchar(128)
);

create table lebenslauf_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
mitgliedsnummer_sewobe bigint,
LEBENSLAUF_ART varchar(128),
LEBENSLAUF_BEGINN date,
LEBENSLAUF_ENDE date, 
LEBENSLAUF_BEZEICHNUNG varchar(128),
LEBENSLAUF_KURZTEXT varchar(128),
LEBENSLAUF_LANGTEXT varchar(128)
);

create table offenerechnungen_stage (
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
KDNR varchar(128),
MITGLIEDSNUMMER_SEWOBE bigint,
RECHNUNGSNUMMER bigint,
RECHNUNGSDATUM date,
ZAHLUNGSZIEL date,
ZAHLWEISE varchar(128),
MAHNSTUFE int,
BEZEICHNUNG varchar(128),
AKTUELLOFFEN int
);