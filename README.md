TheSoftwareProject
==================

To compile and run the program run javac Main.java and then Main.java.

There is no way to interact with the program while it is running. However there are constraints you can change to affect how it runs. The constraints are as follows:

int day = 5400; //time it takes to simulate a day in milliseconds
	                                     //(must represent a 9 hour work day at a minimum)
int minute = 10; //time it takes to simulate a minute in milliseconds
int numTeams = 3; //Number of teams
int numDevsPerTeam = 4; //Number of Devs per team (including leads)
int numRooms = 1; //Number of conference rooms available
int minDevQues = 0; //Minimum amount of questions a dev can ask
int maxDevQues = 2; //Maximum amount questions a dev can ask
int minLeadQues = 0; //Minimum amount of questions a dev can ask
int maxLeadQues = 1; //Maximum amount questions a dev can ask
int managerQuesTime = 10; //Time it takes for a manager to answer each question

These variables are all located in Main.java

The output of the program is all major events at the office followed by statistics about the time worked, time at lunch, time in meetings, and time spent waiting for questions to be answered for each employee.
