START TRANSACTION;

DROP DATABASE IF EXISTS VI_DB;

CREATE DATABASE VI_DB;

USE VI_DB;

CREATE TABLE VI_DB.Employees (  ID int PRIMARY KEY AUTO_INCREMENT,
							    Name TEXT NOT NULL,
							    Surname TEXT NOT NULL,
							    PhoneNumber INTEGER(11),
							    Mail TEXT, 
							    Login VARCHAR(50),
							    Hash TEXT NOT NULL
							     );




CREATE TABLE VI_DB.WaitingContracts(ID int PRIMARY KEY AUTO_INCREMENT,
									VehicleYear int NOT NULL,
									ClientToken TEXT NOT NULL,
									RequestToken TEXT NOT NULL,
									PlateNumber TEXT NOT NULL,
									Date DATE NOT NULL,
									VIN TEXT,
									Model TEXT NOT NULL,
									Brand TEXT,
									InsurancePcg TEXT NOT NULL,
									StorageName TEXT  
									);





COMMIT;