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
							    Hash TEXT NOT NULL,
							    Salt TEXT NOT NULL
							     );


CREATE TABLE VI_DB.StorageTypes(ID int PRIMARY KEY AUTO_INCREMENT,
								 TypeName TEXT NOT NULL);

INSERT INTO VI_DB.StorageTypes (TypeName) VALUES ("OUTDOOR");
INSERT INTO VI_DB.StorageTypes (TypeName) VALUES ("GUARDED OUTDOOR");
INSERT INTO VI_DB.StorageTypes (TypeName) VALUES ("INDOOR");
INSERT INTO VI_DB.StorageTypes (TypeName) VALUES ("GUARDED INDOOR");
INSERT INTO VI_DB.StorageTypes (TypeName) VALUES ("OTHER");

CREATE TABLE VI_DB.PackageTypes(ID int PRIMARY KEY AUTO_INCREMENT,
																TypeName TEXT);

CREATE TABLE VI_DB.InsurancePcg(ID int PRIMARY KEY AUTO_INCREMENT,
								ShortName TEXT NOT NULL,
								FullName TEXT NOT NULL,
								Description TEXT,
								Discount DOUBLE,
								PackageType int, 
								FOREIGN KEY (PackageType) REFERENCES PackageTypes(ID));


CREATE TABLE VI_DB.WaitingContracts(ID int PRIMARY KEY AUTO_INCREMENT,
									VehicleYear int NOT NULL,
									ClientToken TEXT NOT NULL,
									RequestToken TEXT NOT NULL,
									PlateNumber TEXT NOT NULL,
									Date DATE NOT NULL,
									VIN TEXT,
									Model TEXT NOT NULL,
									Brand TEXT,
									StorageType int,
									FOREIGN KEY (StorageType) REFERENCES StorageTypes(ID),
									InsurancePcg int,
									FOREIGN KEY (InsurancePcg) REFERENCES StorageTypes(ID),
									StorageName TEXT  
									);





INSERT INTO VI_DB.PackageTypes (TypeName) VALUES ("OC");
INSERT INTO VI_DB.PackageTypes (TypeName) VALUES ("AC");
INSERT INTO VI_DB.PackageTypes (TypeName) VALUES ("OC+AC");


COMMIT;