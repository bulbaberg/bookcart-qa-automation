USE BookCartDB;
GO

INSERT INTO Categories VALUES('Biography');
INSERT INTO Categories VALUES('Fiction');
INSERT INTO Categories VALUES('Mystery');
INSERT INTO Categories VALUES('Fantasy');
INSERT INTO Categories VALUES('Romance');

INSERT INTO UserType VALUES('Admin');
INSERT INTO UserType VALUES('User');

INSERT INTO UserMaster VALUES('Ankit','Sharma','adminuser','qwerty','Male', 1);
INSERT INTO UserMaster VALUES('Test', 'Buyer', 'userNameTest1', 'Qwerty123', 'Male', 2);
GO