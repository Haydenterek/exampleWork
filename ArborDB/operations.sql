SET SCHEMA 'arbordb';

-- Drop functions and procedures created by this script
DROP FUNCTION IF EXISTS ArborDB.listMaintainedSensors(char(9));
DROP FUNCTION IF EXISTS ArborDB.listSensors(INTEGER);
DROP PROCEDURE IF EXISTS ArborDB.removeSensor(INTEGER);
DROP PROCEDURE IF EXISTS ArborDB.removeWorkerFromState(char(9), char(2));
DROP PROCEDURE IF EXISTS ArborDB.moveSensor(INTEGER, real, real);
DROP PROCEDURE IF EXISTS ArborDB.deleteWorker(char(9));
DROP PROCEDURE IF EXISTS ArborDB.removeSpeciesFromForest(varchar(30), varchar(30), INTEGER);
DROP PROCEDURE IF EXISTS ArborDB.generateReport(INTEGER, timestamp, real);
DROP PROCEDURE IF EXISTS ArborDB.placeSensor(INTEGER, real, real, char(9));
DROP PROCEDURE IF EXISTS ArborDB.employWorkerToState(char(2), char(9));
DROP PROCEDURE IF EXISTS ArborDB.newWorker(char(9), varchar(30), varchar(30), varchar(1), varchar(10), char(2));
DROP PROCEDURE IF EXISTS ArborDB.addSpeciesToForest(INTEGER, varchar(30), varchar(30));
DROP PROCEDURE IF EXISTS ArborDB.addTreeSpecies(varchar(30), varchar(30), real, real, varchar(16));
DROP PROCEDURE IF EXISTS ArborDB.addForest(varchar(30), integer, real, real, real, real, real);
DROP FUNCTION IF EXISTS ArborDB.locateTreeSpecies(genus_pattern varchar(30), epithet_pattern varchar(30));

-- Task number one, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.addForest(name varchar(30), area integer, acid_level real,
MBR_XMin real, MBR_XMax real, MBR_YMin real, MBR_YMax real)
LANGUAGE plpgsql
AS $$
DECLARE
    forest_no INTEGER;
BEGIN
    SELECT COUNT(*) + 1 INTO forest_no FROM ArborDB.forest;
    INSERT INTO ArborDB.forest
    values(forest_no, name, area, acid_level, MBR_XMin, MBR_XMax, MBR_YMin, MBR_YMax);
end;
$$;

-- Task number two, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.addTreeSpecies(genus varchar(30), epithet varchar(30), ideal_temperature real,
largest_height real, raunkiaer_life_form varchar(16))
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ArborDB.Tree_Species
    values(genus, epithet, ideal_temperature, largest_height, raunkiaer_life_form);
end;
$$;

-- Task number three, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.addSpeciesToForest(forest_no INTEGER, genus varchar(30), epithet varchar(30))
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ArborDB.found_in
    values(forest_no, genus, epithet);
end;
$$;

-- Task number five, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.employWorkerToState(abbreviation char(2), SSN char(9))
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ArborDB.employed
    values(abbreviation, SSN);
end;
$$;

-- Task number four, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.newWorker(SSN char(9), First_name varchar(30), Last_name varchar(30),
Middle_initial char(1), rank varchar(10), abbreviation char(2))
LANGUAGE plpgsql
AS $$
DECLARE
    name ArborDB.name;
BEGIN
    -- Create ArborDB.name type from First_name, Last_name, Middle_initial
    name = ROW(First_name, Last_name, Middle_initial);
    -- Insert into ArborDB.worker
    INSERT INTO ArborDB.worker
    values(SSN, name, rank);
    CALL ArborDB.employWorkerToState(abbreviation, SSN);
end;
$$;

-- Task number six, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.placeSensor(energy INTEGER, X real, Y real, maintainer_id char(9))
LANGUAGE plpgsql
AS $$
DECLARE
    sensor_id_s integer;
    last_charged timestamp;
    last_read timestamp;
BEGIN
    UPDATE ArborDB.clock
    SET synthetic_time = CURRENT_TIMESTAMP;
    SELECT synthetic_time into last_charged FROM ArborDB.clock;
    SELECT synthetic_time into last_read FROM ArborDB.clock;
    SELECT COUNT(*) + 1 into sensor_id_s FROM ArborDB.sensor;
    INSERT INTO ArborDB.sensor
    values(sensor_id_s, last_charged, energy, last_read, X, Y, maintainer_id);
end;
$$;

-- Task number seven, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.generateReport(sensor_id INTEGER, report_time timestamp, temperature real)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ArborDB.report
    values(sensor_id, report_time, temperature);
end;
$$;

-- Task number eight, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.removeSpeciesFromForest(genus_P varchar(30), epithet_p varchar(30), forest_no_p INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ArborDB.found_in
    WHERE ArborDB.found_in.forest_no = forest_no_p AND
          ArborDB.found_in.genus = genus_p AND
          ArborDB.found_in.epithet = epithet_p;
end;
$$;

-- Task number nine, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.deleteWorker(SSN_p char(9))
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ArborDB.sensor
    WHERE ArborDB.sensor.maintainer_id = SSN_p;
    DELETE FROM ArborDB.employed
    WHERE ArborDB.employed.worker = SSN_p;
    DELETE FROM ArborDB.worker
    WHERE ArborDB.worker.SSN = SSN_p ;
end;
$$;

-- Task number ten, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.moveSensor(sensor_id_p INTEGER, X_p real, Y_p real)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE ArborDB.sensor
    SET X = X_p, Y = Y_p
    WHERE ArborDB.sensor.sensor_id = sensor_id_p;
end;
$$;

-- Task number eleven
CREATE OR REPLACE PROCEDURE ArborDB.removeWorkerFromState(SSN char(9), abb char(2))
LANGUAGE plpgsql
AS $$
DECLARE
    state_info RECORD;
    sensor_info RECORD;
    lowest_ssn char(9);
BEGIN
    SELECT * INTO state_info
    FROM ArborDB.state
    WHERE state.abbreviation = abb;

    FOR sensor_info IN (SELECT * FROM ArborDB.sensor WHERE sensor.maintainer_id = SSN)
    LOOP
        IF  state_info.abbreviation = abb AND sensor_info.X <= state_info.mbr_xmax AND sensor_info.X >= state_info.mbr_xmin AND
            sensor_info.Y <= state_info.mbr_ymax AND sensor_info.Y <= state_info.mbr_ymax THEN
            SELECT MIN(maintainer_id) INTO lowest_ssn FROM ArborDB.sensor;
            UPDATE ArborDB.sensor
            SET maintainer_id = lowest_ssn
            WHERE sensor.sensor_id = sensor_info.sensor_id;
        ELSE
            DELETE FROM ArborDB.report
            WHERE report.sensor_id = sensor_info.sensor_id;
            DELETE FROM ArborDB.sensor
            WHERE sensor.sensor_id = sensor_info.sensor_id;
        end if;
    END LOOP;

    DELETE FROM ArborDB.employed
    WHERE ArborDB.employed.worker = SSN;
end;
$$;

-- Task number twelve, COMPLETED
CREATE OR REPLACE PROCEDURE ArborDB.removeSensor(sensor_id_p INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ArborDB.report
    WHERE sensor_id = sensor_id_p;
    DELETE FROM ArborDB.sensor
    WHERE sensor_id = sensor_id_p;
end;
$$;

-- Task number thirteen, COMPLETED
CREATE OR REPLACE FUNCTION ArborDB.listSensors(forest_id INTEGER)
RETURNS TABLE (
    sensor_id INTEGER,
    last_charged TIMESTAMP,
    energy INTEGER,
    last_read TIMESTAMP,
    X REAL,
    Y REAL,
    maintainer_id char(9)
)
LANGUAGE plpgsql
AS $$
DECLARE
    forest RECORD;
BEGIN
    SELECT * INTO forest
    FROM ArborDB.forest
    WHERE ArborDB.forest.forest_no = forest_id;
    RETURN QUERY SELECT sensor.sensor_id, sensor.last_charged, sensor.energy, sensor.last_read, sensor.X, sensor.Y,
                        sensor.maintainer_id
    FROM ArborDB.sensor
    WHERE sensor.X <= forest.mbr_xmax AND
          sensor.X >= forest.mbr_xmin AND
          sensor.Y <= forest.mbr_ymax AND
          sensor.Y >= forest.mbr_ymin;
end;
$$;

-- Task number fourteen, COMPLETED
CREATE OR REPLACE FUNCTION ArborDB.listMaintainedSensors(SSN char(9))
RETURNS TABLE (
    sensor_id INTEGER,
    last_charged TIMESTAMP,
    energy INTEGER,
    last_read TIMESTAMP,
    X REAL,
    Y REAL,
    maintainer_id char(9)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT sensor.sensor_id, sensor.last_charged, sensor.energy, sensor.last_read, sensor.X, sensor.Y,
                        sensor.maintainer_id
    FROM ArborDB.sensor
    WHERE sensor.maintainer_id = SSN;
end;
$$;

-- Task number fifteen
CREATE OR REPLACE FUNCTION ArborDB.locateTreeSpecies(genus_pattern varchar(30), epithet_pattern varchar(30))
RETURNS TABLE (
    forest_no INTEGER,
    name varchar(30),
    area INTEGER,
    acid_level REAL,
    MBR_XMin REAL,
    MBR_XMax REAL,
    MBR_YMin REAL,
    MBR_YMax REAL
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT forest.forest_no, forest.name, forest.area, forest.acid_level, forest.MBR_XMin, forest.MBR_XMax,
                        forest.MBR_YMin, forest.MBR_YMax
    FROM ArborDB.forest
    WHERE EXISTS (SELECT * FROM ArborDB.found_in WHERE ArborDB.found_in.forest_no = ArborDB.forest.forest_no AND
                  ArborDB.found_in.genus LIKE genus_pattern AND ArborDB.found_in.epithet LIKE epithet_pattern);
end;
$$;

-- Additional Functions Not Required by the Project
CREATE OR REPLACE PROCEDURE ArborDB.removeAllSensors()
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ArborDB.sensor;
end;
$$;

CREATE OR REPLACE FUNCTION ArborDB.listAllSensors()
RETURNS TABLE (
    sensor_id INTEGER,
    last_charged TIMESTAMP,
    energy INTEGER,
    last_read TIMESTAMP,
    X REAL,
    Y REAL,
    maintainer_id char(9)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT sensor.sensor_id, sensor.last_charged, sensor.energy, sensor.last_read, sensor.X, sensor.Y,
                        sensor.maintainer_id
    FROM ArborDB.sensor;
end;
$$;
