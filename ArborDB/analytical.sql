SET SCHEMA 'arbordb';

DROP VIEW IF EXISTS ArborDB.RankForestSensors;
CREATE VIEW ArborDB.RankForestSensors AS
SELECT
  f.forest_no, f.name, COUNT(s.sensor_id) AS sensor_count
FROM
  ArborDB.forest f
LEFT JOIN
  ArborDB.sensor s
ON
  f.MBR_XMIN <= s.x AND
  f.MBR_XMAX >= s.x AND
  f.MBR_YMIN <= s.y AND
  f.MBR_YMAX >= s.y
GROUP BY
  f.forest_no,
  f.name
ORDER BY
  sensor_count DESC;

DROP FUNCTION IF EXISTS ArborDB.topSensors(INTEGER, INTEGER);
CREATE OR REPLACE FUNCTION ArborDB.topSensors(x INTEGER, k INTEGER)
RETURNS TABLE (
    sensor_id INTEGER,
    count BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    clock_time timestamp;
    newTime interval;
BEGIN
    SELECT synthetic_time into clock_time FROM ArborDB.clock;
    x := x * 30;
    newTime := x * INTERVAL '1 DAY';
    clock_time := clock_time - newTime;
    RETURN QUERY SELECT ArborDB.report.sensor_id, COUNT(*) as count
    FROM ArborDB.report
    WHERE report_time >= clock_time
    GROUP BY report.sensor_id
    ORDER BY count DESC
    LIMIT k;
end;
$$;

DROP FUNCTION IF EXISTS ArborDB.habitableEnvironments(genus VARCHAR(30), epithet VARCHAR(30), k INTEGER);

CREATE OR REPLACE FUNCTION ArborDB.habitableEnvironments(genus_p VARCHAR(30), epithet_p VARCHAR(30), k INTEGER)
RETURNS TABLE (
    forest_no_f INTEGER,
    forest_name VARCHAR(30),
    genus_f VARCHAR(30),
    epithet_f VARCHAR(30),
    temp_f REAL
)
LANGUAGE plpgsql
AS $$
DECLARE
    forest RECORD;
    sensor RECORD;
    newTime interval;
    clock_time timestamp;
    temp REAL;
    ideal_t REAL;
BEGIN
    SELECT synthetic_time into clock_time FROM ArborDB.clock;
    k := k * 365;
    newTime := k * INTERVAL '1 DAY';
    clock_time := clock_time - newTime;
    FOR forest IN SELECT * FROM ArborDB.forest LOOP
        SELECT * FROM listSensors(forest.forest_no) INTO sensor;
        SELECT temperature INTO TEMP FROM ArborDB.report
        WHERE sensor_id = sensor.sensor_id AND report_time >= clock_time;

        SELECT ideal_temperature INTO ideal_t
        FROM ArborDB.tree_species
        WHERE ArborDB.tree_species.genus = genus_p AND ArborDB.tree_species.epithet = epithet_p;

        IF(temp >= ideal_t - 5 AND temp <= ideal_t + 5) THEN
            RETURN QUERY SELECT forest.forest_no, forest.name, genus_p, epithet_p, temp;
        end if;
    end loop;
end;
$$;

DROP FUNCTION IF EXISTS ArborDB.threeDegrees(f1 INTEGER, f2 INTEGER);
CREATE OR REPLACE FUNCTION ArborDB.threeDegrees(f1 INTEGER, f2 INTEGER)
-- Return a string of the path between f1 and f2
RETURNS VARCHAR(100)
LANGUAGE plpgsql
AS $$
    DECLARE
        common_species_cursor CURSOR FOR
        SELECT forest1.forest_no, forest1.genus, forest1.epithet
        FROM ArborDB.found_in forest1
        JOIN ArborDB.found_in forest2 ON forest1.genus = forest2.genus AND forest1.epithet = forest2.epithet
        WHERE forest1.forest_no = f1
          AND forest2.forest_no = f2;

        f INTEGER;
        hops INTEGER;

        common_species arbordb.found_in%ROWTYPE;

        path VARCHAR(100);
    BEGIN

        -- If f1 = f2 then return 'No path exists'
        IF f1 = f2 THEN
            RETURN 'Same forest given';
        END IF;

        -- If f1 or f2 does not exist then return 'Invalid forest_no'
        SELECT * INTO f FROM ArborDB.forest WHERE forest_no = f1;
        IF NOT FOUND THEN
            RETURN 'Invalid forest_no';
        END IF;
        SELECT * INTO f FROM ArborDB.forest WHERE forest_no = f2;
        IF NOT FOUND THEN
            RETURN 'Invalid forest_no';
        END IF;

        hops := 0;
        path := f1 || ' -> ';

        OPEN common_species_cursor;

        -- Find up to 3 forests that have a species in common_species_cursor
        LOOP
            FETCH common_species_cursor INTO common_species;
            EXIT WHEN hops = 3;
            IF NOT FOUND THEN
                RETURN 'No path exists';
            END IF;
            -- Find all forests that have the same species as common_species
            FOR f IN SELECT forest_no FROM ArborDB.found_in WHERE genus = common_species.genus AND epithet = common_species.epithet LOOP
                -- If f = f2 then return path
            IF NOT FOUND THEN
                EXIT;
            END IF;
            IF f = f2 OR f = f1 THEN
                CONTINUE;
            END IF;

            hops := hops + 1;
            path := path || f || ' -> ';

            -- If hops = 3 then return path
            IF hops = 3 THEN
                RETURN path || f2;
            END IF;

            END LOOP;


            -- Add up to the first 3 forests to the path
        END LOOP;

        CLOSE common_species_cursor;

        -- if hops = 0 then return 'No path exists'

        RETURN path || f2;

    end;
$$;