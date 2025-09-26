-- Vehicle Types
INSERT INTO `type` (type_name) VALUES ('MOTOR_HOME');
INSERT INTO `type` (type_name) VALUES ('CARAVAN');
INSERT INTO `type` (type_name) VALUES ('TRAILER');
INSERT INTO `type` (type_name) VALUES ('TRUCK_CAMPER');
INSERT INTO `type` (type_name) VALUES ('ETC');

-- Models
-- MOTOR_HOME
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '1톤축연장');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '1톤축미연장');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '칸');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '마스터');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '스타렉스');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '스타리아');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'MOTOR_HOME'), '카운티');

-- CARAVAN
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'CARAVAN'), '300급');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'CARAVAN'), '400급');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'CARAVAN'), '500급');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'CARAVAN'), '600급');

-- TRAILER
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'TRAILER'), '폴딩형');
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'TRAILER'), '카고형');

-- TRUCK_CAMPER
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'TRUCK_CAMPER'), '그외');

-- ETC
INSERT INTO model (type_id, model_name) VALUES ((SELECT type_id FROM `type` WHERE type_name = 'ETC'), '기타');

-- Engines
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('GASOLINE', 'MANUAL', 150, 10.0);
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('GASOLINE', 'AUTOMATIC', 150, 10.0);
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('DIESEL', 'MANUAL', 180, 12.5);
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('DIESEL', 'AUTOMATIC', 180, 12.5);
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('ELECTRIC', 'AUTOMATIC', 200, 0);
INSERT INTO `engine` (feul_type, transmission, horse_power, fuel_efficiency) VALUES ('HYBRID', 'AUTOMATIC', 190, 15.0);


-- Cars
INSERT INTO `car` (model_id, engine_id) SELECT m.model_id, e.engine_id FROM model m, engine e;
