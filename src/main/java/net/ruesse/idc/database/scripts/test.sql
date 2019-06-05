SELECT 
                   "IDCREMOTE"."PERSON"."MGLNR",
	           "IDCREMOTE"."PERSON"."VORNAME",
	           "IDCREMOTE"."PERSON"."NACHNAME",
                   "IDCREMOTE"."PERSON"."HAUPTKATEGORIE",
                   "IDCLOCAL"."CARD"."PRFMGLNR"
                   FROM "IDCREMOTE"."PERSON"  JOIN "IDCLOCAL"."CARD" 
                   ON "IDCREMOTE"."PERSON"."MGLNR"="IDCLOCAL"."CARD"."MGLNR"
                   WHERE 
	             "IDCREMOTE"."PERSON"."STATUS" = 'Aktiv'
	             