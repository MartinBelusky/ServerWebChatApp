# ServerWebChatApp
This App is standart server socket application. It is used for chatting between different users on different computers.
There is also a ChatBot, which understands some commnads and clients can talk to him.

In this project, we have 4 classes, that can be run:
1. **Server class**: this class is the most important. It is one end of communication and all clients are connencted to it. It can be run on server or on comupter.

How does it work:
First, user has to difine port number, from which all messages will go through.
Then in an infinite loop, the servers wait for users to connect (Socket socket = serverSocket.accept();)
It is a blocking command.
If there is a new connection a new Thread is created and started.
In first step, server wait until user set his valid name and then notifies all connected users about new connection.

2. **Client class**: this class is for user to connect to server and to chat through console.

How does it work:
We create a new Thread and start it. Right after the start, the algorithm stops other commands(chatting): (synchronized(this) {wait();}) until it:
- gets server address,
- gets port number,
- gets valid user name.
If all this steps are done, algorithm unlocks the lock (synchronized(Client.this) {Client.this.notify();}) and start chatting with other users.

3. **BotClient class**: this class is a pre-programmed botchat client, who understands basic commands about time and dates. The logic is the same as in Client class, but much simpler.

4. **ClientGuiController class**: it is the same as Client class, but the communication isn't done through console, but with swing app.

Example of use:
1. start a server class, set a port number.
2. start a client class (or ClientGuiController class), as a server address, write: _localhost_, set a port number.
3. start a botclient class and do the same steps.
4. You can chat.

**Videotutotorial**:
https://www.youtube.com/watch?v=6tQcTlJFV0s
