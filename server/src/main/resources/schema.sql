DROP TABLE IF EXISTS USER_ACCOUNT;
CREATE TABLE USER_ACCOUNT (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  USERNAME VARCHAR NOT NULL,
  PASSWORD VARCHAR NOT NULL,
  SPRITE VARCHAR NOT NULL,
  DIRECTION VARCHAR NOT NULL,
  MAP_AREA INT NOT NULL,
  X INT NOT NULL,
  Y INT NOT NULL,
  SPEED INT NOT NULL,
  LAST_LOGIN TIMESTAMP
);