Pong
====
By: Joshua Mahony (10829953) jm426@uni.brighton.ac.uk

Instructions for running the pong coursework

Server.jar command line options

-p the port for the server to use

Example:

java -jar Server.jar

Setting up a basic game with delay compensation
===============================================
1. Start the server with the command

   java -jar Server.jar -p 50001

2. Start a client with the command in a new window

   java -jar Client.jar -p 50001 -h localhost

3. Start another client with the command in a new window

   java -jar Client.jar -p 50001 -h localhost


Setting up a multicast game
===========================
1. Start the server with the command

   java -jar Server.jar -p 50001
