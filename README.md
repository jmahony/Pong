Pong
====
By: Joshua Mahony (10829953) jm426@uni.brighton.ac.uk

Instructions for running the pong coursework

Note: The server will crash if a client disconnects.

Server.jar command line options

-p the port for the server to use

Client.jar command line options
-p <port> the port of the server
-h <host> the hostname of the server
-m start a multicast game
-s <port> start a spectator needs the the port to listen on


Setting up a basic game with delay compensation (TCP games have it turned on by default)
===============================================
1. Start the server with the command

   java -jar Server.jar -p 50001

2. Start a client in a new window with the command

   java -jar Client.jar -p 50001 -h localhost

2. Start another client in a new window with the command

   java -jar Client.jar -p 50001 -h localhost


Setting up a multicast game
===========================
1. Start the server with the command

   java -jar Server.jar -p 50001

2. Start a client in a new window with the command

   java -jar Client.jar -p 50001 -h localhost -m

3. Start another client in a new window with the command

   java -jar Client.jar -p 50001 -h localhost -m

4. Start a spectator client in a new window

   java -jar Client.jar -s 50002
