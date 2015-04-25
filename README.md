# testing-app
freelance project

##purpose
Application allows you to create and pass tests, and also view results. 
There are two access modes: for admins and students.
Admin can add new tests, edit existing tests and view results
Student can pass tests

##features
Tests may contain different amount of questions. Admin have to specify number of question per session (they will be chosen randomly for tests) and minimum number for passing the test. Admin can make test available/non-availabe and set password for it.

Test Question must contain question itself and answer variant (number of variants may vary). Test question may also contain picture

Admin can change passwords for login

Admin can view / remove results of passing the tests

Student before passing the test must enter his/her name, surname and password for test (if required)


##architecture
Program implemented using MVC pattern. Model classes, as you may have guessed, are situated in package **dev.vmykh.testingapp.model**

Views and Controllers are in same package (requirement of JavaFX) **dev.vmykh.testingapp.view**

there are also a bunch of unit tests for model

##security
tests, test sessions and passwords are saved using serialization and encrypting to prevent crafty students from breaking into program

##running the program
This is eclipse JavaFX project, so if you want to run it's recommended to have eclipse with appropriate JavaFX plugin installed (not surprisingly)

You can also encounter some problems with text alignment, because linux and windows are using different fonts.

You should also change variable RESOURCE_DIR (it specify directory for saving data) in class dev.vmykh.testingapp.model.ResourceBundle. 
However, if you are using linux and your username is mrgibbs you may keep it same :)





