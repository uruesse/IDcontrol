DROP TABLE IDCLOCAL.USERPREF;
DROP TABLE IDCLOCAL.CARD;

CREATE TABLE IDCLOCAL.USERPREF (
    MGLNR BIGINT PRIMARY KEY,
    PRINTER VARCHAR(128),
    PDFHEIGHT INT
);

CREATE TABLE IDCLOCAL.CARD (
    MGLNR BIGINT PRIMARY KEY,
    DATEOFISSUE DATE,
    PRFMGLNR BIGINT,
    ISSUE INT
);