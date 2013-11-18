ChatApplication
===============

A simple Server and Client command line chat program in Java
Server usage - java Server [port]
Client usage - java Client [host] [port]

The server has 4 classes -
>Server - 
this is the main class, which initialises the server port and contains the shutdown hook for the program so everything can be closed properly. It also runs the first thread of the program, contained in...
>ConnectionThread - 
this class contains the thread which handles connections to the server. Once a connection is made to the port, new MessageThreads and OutgoingThreads are made and stored in their corresponding arraylists.
>MessageThread - 
this class takes in all messages received by the server from the client associated with the thread (each user gets their own, lucky them!). When a message is received, it is printed to the Server's standard output, and it is also placed in the message queue of each of the other users. A confirmation message will also be added to this client's message queue, so the client can tell when a message is received. This thread also handles special messages like :users, which will print the current user list to the client, and //disconnect//, which is used when the client closes.
>OutgoingThread - 
the last class of the server handles sending the messages to the client (at least after they have connected). This thread will iterate through the message queue for it's client and print them to the client's screen. It will also print a //closed// message on server close, so alert the client.

The client has 3 classes -
>Client - 
this is the main class for this end of the system, and handles the initial connection to the server and execution of the other threads. Once the initial handshaking has taken place, all communication is handed over to the MessageThread and ReplyThread. This class also stores whether a message was received by the server. There is also a similar shutdown hook in this class to on the Server end, so the system can close properly on exit.
>MessageThread - 
this thread handles all the incoming messages to the client. Unless they are one of the special messages, they are simply printed to the terminal. Special messages are also handled accordingly by this class.
>ReplyThread - 
this last thread handles outgoing messages from the client. This simply takes messages from the standard input and sends them to the server. The thread will then wait for the server to signal that they received the message (handled by the MessageThread setting the client's OK boolean to true for a short time) before continuing. This thread will also send the //disconnect// message to the server when the client shuts down.
