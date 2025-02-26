CALL addForest('test forest', 2, 2, 2, 2, 2, 2);
SELECT * from ArborDB.forest;

CALL addtreespecies('agenus', 'anotherRandom', 44,
4, 'Hemicryptophytes');
SELECT * FROM ArborDB.tree_species;

CAll addSpeciesToForest(91, 'agenus', 'anotherRandom');
SELECT * FROM ArborDB.found_in;

CALL newWorker('111231312', 'Hayden','Terek','D', 'Senior', 'PA');
SELECT * FROM ArborDB.worker;
SELECT * FROM ArborDB.state;

CALL employworkertostate('PA', '911277048');
SELECT * FROM ArborDB.employed;

-- INSERT INTO ArborDB.clock(synthetic_time)
--VALUES(CURRENT_TIMESTAMP);
SELECT * FROM ArborDB.clock;

CALL placeSensor(10, 3, 3, '111131312');
SELECT * FROM ArborDB.sensor;

CALL generateReport(182, LOCALTIMESTAMP, 43);
SELECT * FROM ArborDB.report;

INSERT INTO ArborDB.found_in(forest_no, genus, epithet)
VALUES(1, 'Larix', 'Lyallii');
INSERT INTO ArborDB.found_in(forest_no, genus, epithet)
VALUES(22, 'Larix', 'Laricina');

SELECT * FROM ArborDB.tree_species;

SELECT * FROM ArborDB.found_in;

CALL removespeciesfromforest('Larix', 'Lyallii', 1);
SELECT * FROM ArborDB.found_in;

SELECT * FROM ArborDB.worker;
SELECT * FROM ArborDB.employed;
SELECT * FROM ArborDB.sensor;

CALL deleteworker('111231312');

SELECT * FROM ArborDB.worker;
SELECT * FROM ArborDB.employed;
SELECT * FROM ArborDB.sensor;

CALL movesensor(1, 10, 10);
SELECT * FROM ArborDB.sensor;

CALL removeSensor(182);
SELECT * FROM ArborDB.sensor;
SELECT * FROM ArborDB.report;

SELECT * FROM listmaintainedsensors('101495857');

SELECT * FROM listsensors(1);

-- Tests for task 11
CALL newWorker('111131342', 'Hayden','Terek','D', 'Senior', 'PA');
CALL newWorker('111131352', 'Hayden','Terek','D', 'Senior', 'FL');
CALL newWorker('111131362', 'Hayden','Terek','D', 'Senior', 'MA');

CALL removeworkerfromstate('111131362', 'MA');

CALL employworkertostate('PA', '101495857');

CALL removeworkerfromstate('101495857', 'PA');

CALL employworkertostate('PA', '109442154');

CALL removeworkerfromstate('109442154', 'PA');

CALL employworkertostate('PA', '127549380');

CALL removeworkerfromstate('127549380', 'PA');
-- 67 and 166 should change 130396991
-- 5 - 405 43 - 3

CALL employworkertostate('PA', '130396991');
CALL placeSensor(10, 461, 120, '130396991');
CALL removeworkerfromstate('130396991', 'PA');

SELECT * FROM ArborDB.state;
SELECT * FROM ArborDB.employed;
SELECT * FROM ArborDB.sensor;
