-- Create Triggers for ArborDB
SET SCHEMA 'arbordb';

-- Drop triggers if they exist
DROP TRIGGER IF EXISTS addForestCoverage ON ArborDB.coverage;
DROP TRIGGER IF EXISTS checkMaintainerEmployment ON ArborDB.sensor;

-- Trigger to check if the forest is in the state
CREATE OR REPLACE FUNCTION ArborDB.addForestCoverage() RETURNS TRIGGER AS
$$
DECLARE
    state   ArborDB.state%ROWTYPE; -- State of the forest
    forest  ArborDB.forest%ROWTYPE; -- Forest to be added
    x_dist    REAL; -- Distance between the X coordinates of the MBRs
    y_dist    REAL; -- Distance between the Y coordinates of the MBRs
BEGIN
    -- Check if MBR of state and forest are zero
    -- Get the state MBR using the abbreviation passed in
    SELECT * INTO state FROM ArborDB.state e WHERE e.abbreviation = NEW.state;
    -- Get the forest MBR using the forest name passed in
    SELECT * INTO forest FROM ArborDB.forest e WHERE e.forest_no  = NEW.forest_no;

    -- Check if the MBRs are zero which implies area is zero
    IF ((state.mbr_xmin = state.mbr_xmax OR state.mbr_ymin = state.mbr_ymax)
        OR (forest.mbr_xmin = forest.mbr_xmax OR forest.mbr_ymin = forest.mbr_ymax)) THEN
        RETURN NEW;
    ELSE
        -- Check if the MBRs overlap
        IF (forest.mbr_xmin > state.mbr_xmax OR state.mbr_xmin > forest.mbr_xmax) THEN
            RETURN NEW;
        ELSE
            IF (forest.mbr_ymin > state.mbr_ymax OR state.mbr_ymin > forest.mbr_ymax) THEN
                RETURN NEW;
            ELSE
                -- If none are true, then the MBRs overlap, so calculate the area
                x_dist = LEAST(forest.mbr_xmax, state.mbr_xmax) - GREATEST(forest.mbr_xmin, state.mbr_xmin);
                y_dist = LEAST(forest.mbr_ymax, state.mbr_ymax) - GREATEST(forest.mbr_ymin, state.mbr_ymin);
                NEW.area = x_dist * y_dist;

                -- Calculate the percentage of the state covered by the forest as a decimal
                NEW.percentage = NEW.area / ((state.mbr_xmax - state.mbr_xmin) * (state.mbr_ymax - state.mbr_ymin));

                RETURN NEW;
            END IF;
        END IF;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER addForestCoverage
    BEFORE INSERT
    ON ArborDB.coverage
    FOR EACH ROW
EXECUTE FUNCTION ArborDB.addForestCoverage();

-- Trigger to check if the sensor is in the state of the maintainer
CREATE OR REPLACE FUNCTION ArborDB.checkMaintainerEmployment() RETURNS TRIGGER AS
$$
DECLARE
    state  ArborDB.state%ROWTYPE; -- State of the maintainer
    num_iterations  INTEGER; -- Number of iterations in the loop
BEGIN
    -- Loop through all states that the maintainer is employed by and check if the sensor is in that state
    FOR state IN SELECT * FROM ArborDB.state WHERE ArborDB.state.abbreviation IN
        (SELECT ArborDB.employed.state FROM ArborDB.employed WHERE ArborDB.employed.worker = NEW.maintainer_id)
    LOOP
        -- Check if the sensor is in the state of the maintainer
        IF (NEW.X >= state.mbr_xmin AND NEW.X <= state.mbr_xmax OR NEW.Y >= state.mbr_ymin AND
            NEW.Y <= state.mbr_ymax) THEN
            RETURN NEW;
        END IF;
    END LOOP;

    -- Sensor not in the state of the maintainer, so raise an exception
    RAISE NOTICE 'The new maintainer of this sensor is not employed by a state which covers the
                    sensor. This operation has been reverted.';
    RETURN NULL;

END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER checkMaintainerEmployment
    BEFORE INSERT OR UPDATE
    ON ArborDB.sensor
    FOR EACH ROW
EXECUTE FUNCTION ArborDB.checkMaintainerEmployment();

-- Find all states that the forest is in
CREATE OR REPLACE FUNCTION ArborDB.checkForestMBR() RETURNS TRIGGER AS
$$
DECLARE
    states ArborDB.state%ROWTYPE; -- State of the forest
BEGIN
    -- If a state MBR overlaps with the forest MBR, then add forest and the state to the coverage table
    FOR states IN SELECT * FROM ArborDB.state e WHERE e.mbr_xmin <= NEW.mbr_xmin AND
        e.mbr_xmax >= NEW.mbr_xmax AND e.mbr_ymin <= NEW.mbr_ymin AND
        e.mbr_ymax >= NEW.mbr_ymax
    LOOP
        INSERT INTO ArborDB.coverage VALUES (NEW.forest_no, states.abbreviation, 0, 0);
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER checkForestMBR
    AFTER INSERT
    ON ArborDB.forest
    FOR EACH ROW
EXECUTE FUNCTION ArborDB.checkForestMBR();

