/*
** IDCREMOTE: aufbereitete Read-Only Datenaus der Vereinsverwaltung. Schema kann in INMEMORY gehalten werden.
** IDCLOCAL: Lokele Datenhaltung
** IDCSTAGE: Stagingbereich - enthält Originaldaten aus der Vereinsverwaltung. Daten können/sollten nach dem Import und der
**           Aufbereitung gelöscht werden. Diese Daten werden ausschließlich während des Stagingprozesses verwendet.
*/
DROP SCHEMA IDCREMOTE RESTRICT;
DROP SCHEMA IDCLOCAL RESTRICT;
DROP SCHEMA IDCSTAGE RESTRICT;
CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.idc', 'idcpass');
CREATE SCHEMA IDCREMOTE AUTHORIZATION idc;
CREATE SCHEMA IDCLOCAL AUTHORIZATION idc;
CREATE SCHEMA IDCSTAGE AUTHORIZATION idc;