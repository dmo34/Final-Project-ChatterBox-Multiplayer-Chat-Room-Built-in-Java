Deliverables (to be done):
1. Live and Unique User list - Show updated/live list of chat members connected at all times. User names must be unique.
       [2/2 DONE] Added system that shows live list of connected users whenever a user joins or leaves. Added unique user names in the form of hashmap key-value pairs.
   
3. Global and Local Chats - Allow messages to either be global (every member can see them) or DM (only the intended recipient and sender can see them).
       [DONE] - Global messages by default, DM feature might be added later.
   
5. Persistent Chats - Chats should be stored on the server and able to be reloaded if server shuts down or client reconnects. [DONE] Chats and conn/disconn info are stored in a txt file on server, client sees reloaded chat history when they join/reconnect.

Also, added GUI for client side.

How to start the server as well as connect as a client:
       compile and run ServerMain.java, server will notify that it has started listening
       compile and run ClientMain.java, GUI will appear asking you to enter name, client can disconnect by closing window
