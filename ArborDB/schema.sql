DROP SCHEMA IF EXISTS ArborDB CASCADE;
CREATE SCHEMA ArborDB;
SET SCHEMA 'arbordb';

-- Drop All Types
DROP TYPE IF EXISTS ArborDB.MBR CASCADE;
DROP TYPE IF EXISTS ArborDB.name CASCADE;

-- Drop All Domains
DROP DOMAIN IF EXISTS ArborDB.rank CASCADE;
DROP DOMAIN IF EXISTS ArborDB.Raunkiaer_life_form CASCADE;

-- Drop All Tables
DROP TABLE IF EXISTS ArborDB.forest CASCADE;
DROP TABLE IF EXISTS ArborDB.state CASCADE;
DROP TABLE IF EXISTS ArborDB.worker CASCADE;
DROP TABLE IF EXISTS ArborDB.phone CASCADE;
DROP TABLE IF EXISTS ArborDB.employed CASCADE;
DROP TABLE IF EXISTS ArborDB.coverage CASCADE;
DROP TABLE IF EXISTS ArborDB.sensor CASCADE;
DROP TABLE IF EXISTS ArborDB.report CASCADE;
DROP TABLE IF EXISTS ArborDB.Tree_Species CASCADE;
DROP TABLE IF EXISTS ArborDB.Tree_Common_Name CASCADE;
DROP TABLE IF EXISTS ArborDB.found_in CASCADE;
DROP TABLE IF EXISTS ArborDB.clock CASCADE;

-- Compounded name
CREATE TYPE ArborDB.name AS (
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    middle_initial CHAR(1)
);

-- Ranks are Lead, Senior, Associate
CREATE DOMAIN ArborDB.rank AS VARCHAR(10)
    CHECK (VALUE IN ('Lead', 'Senior', 'Associate'));

-- Restricted to the 6 Raunkiaer life forms
CREATE DOMAIN ArborDB.Raunkiaer_life_form AS VARCHAR(16)
    CHECK (VALUE IN ('Phanerophytes', 'Epiphytes', 'Chamaephytes', 'Hemicryptophytes', 'Cryptophytes', 'Therophytes', 'Aerophytes'));

-- Complete
CREATE TABLE ArborDB.forest (
    forest_no INTEGER NOT NULL,
    name VARCHAR(30) NOT NULL,
    area INTEGER NOT NULL,
    acid_level REAL NOT NULL,
    MBR_XMin REAL NOT NULL,
    MBR_XMax REAL NOT NULL,
    MBR_YMin REAL NOT NULL,
    MBR_YMax REAL NOT NULL,
    PRIMARY KEY (forest_no)
);

-- Complete
CREATE TABLE ArborDB.state (
    name VARCHAR(30) UNIQUE NOT NULL,
    abbreviation CHAR(2) NOT NULL,
    area INTEGER NOT NULL,
    population INTEGER NOT NULL,
    MBR_XMin REAL NOT NULL,
    MBR_XMax REAL NOT NULL,
    MBR_YMin REAL NOT NULL,
    MBR_YMax REAL NOT NULL,
    PRIMARY KEY (abbreviation)
);

-- Complete
CREATE TABLE ArborDB.worker (
    SSN char(9) NOT NULL,
    name ArborDB.name NOT NULL,
    rank ArborDB.rank NOT NULL,
    PRIMARY KEY (SSN)
    -- CHECK (SSN ~ '^[0-9]+$')
);

-- Complete
CREATE TABLE ArborDB.phone (
    worker CHAR(9) NOT NULL,
    type VARCHAR(30) NOT NULL,
    phone_number VARCHAR(16) UNIQUE NOT NULL,
    PRIMARY KEY (phone_number),
    FOREIGN KEY (worker) REFERENCES ArborDB.worker (SSN) ON DELETE CASCADE
    -- CHECK (phone_number ~ '^[0-9-+]+$')
);

-- Complete
CREATE TABLE ArborDB.employed (
    state VARCHAR(2) NOT NULL,
    worker CHAR(9) NOT NULL,
    PRIMARY KEY (state, worker),
    FOREIGN KEY (state) REFERENCES ArborDB.state (abbreviation) ON DELETE CASCADE,
    FOREIGN KEY (worker) REFERENCES ArborDB.worker (SSN) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.coverage (
    forest_no INTEGER NOT NULL,
    state VARCHAR(2) NOT NULL,
    percentage REAL NOT NULL,
    area INTEGER,
    PRIMARY KEY (forest_no, state),
    FOREIGN KEY (forest_no) REFERENCES ArborDB.forest (forest_no) ON DELETE CASCADE,
    FOREIGN KEY (state) REFERENCES ArborDB.state (abbreviation) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.sensor (
    sensor_id INTEGER NOT NULL,
    last_charged TIMESTAMP NOT NULL,
    energy INTEGER NOT NULL,
    last_read TIMESTAMP NOT NULL,
    X REAL NOT NULL,
    Y REAL NOT NULL,
    maintainer_id CHAR(9) NOT NULL,
    PRIMARY KEY (sensor_id),
    FOREIGN KEY (maintainer_id) REFERENCES ArborDB.worker (SSN) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.report (
    sensor_id INTEGER NOT NULL,
    report_time TIMESTAMP NOT NULL,
    temperature REAL NOT NULL,
    primary key (sensor_id, report_time),
    FOREIGN KEY (sensor_id) REFERENCES ArborDB.sensor (sensor_id) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.Tree_Species (
    genus VARCHAR(30) NOT NULL,
    epithet VARCHAR(30) NOT NULL,
    ideal_temperature REAL NOT NULL,
    largest_height REAL NOT NULL,
    raunkiaer_life_form ArborDB.Raunkiaer_life_form NOT NULL,
    PRIMARY KEY (genus, epithet)
);

-- Complete
CREATE TABLE ArborDB.Tree_Common_Name (
    genus VARCHAR(30) NOT NULL,
    epithet VARCHAR(30) NOT NULL,
    common_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (genus, epithet, common_name),
    FOREIGN KEY (genus, epithet) REFERENCES ArborDB.Tree_Species (genus, epithet) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.found_in (
    forest_no INTEGER NOT NULL,
    genus VARCHAR(30) NOT NULL,
    epithet VARCHAR(30) NOT NULL,
    PRIMARY KEY (forest_no, genus, epithet),
    FOREIGN KEY (forest_no) REFERENCES ArborDB.forest (forest_no) ON DELETE CASCADE,
    FOREIGN KEY (genus, epithet) REFERENCES ArborDB.Tree_Species (genus, epithet) ON DELETE CASCADE
);

-- Complete
CREATE TABLE ArborDB.clock (
    synthetic_time TIMESTAMP UNIQUE NOT NULL,
    PRIMARY KEY (synthetic_time)
);
