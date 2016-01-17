-----------
Simple TODO
-----------

This is a simple fun project that provides interface to create TODO items in Android.


Simple TODO was developed in preparation for the codepath submission and 
is very rudimentary in nature considering a novice android developer
with 3 days experience.

This project was developed for purely learning purposes and 
has no commercial value whatosever. 

If you decide to install and use it for real, please
do play with the TodoItem expiry alarm settings for more
realistic experiences. (Details Below)

A gif file capturing the usage can be found in the same path as this README file.
please check for Todo.gif.

----------------
Functionalities
----------------

1. Custom List Adapter for listing the todo items.
    Option to edit and setup the expiry date and time for each todoitem (via short tap )
    Each row shows a emoticon (angry, happy or sad) as an indicator of the todo item progress
    Option to delete an todo item via long tap

2. Sqlite databse support for storing todo list items.

3. Intent Service for monitoring the todo items for expiry
   Each item is checked for 2 time ranges (SAD and ANGRY) to decide on the 
   icon to be displayed. 
   An SAD emoticon is displayed if the item is about to expire in 15 seconds
   An ANGRY emoticon is displayed if the item is about to expire in 5 seconds.

4. Periodic Alarm and  Broadcast service for invoking the monitoring service
   The code currently triggers the monitoring service every 10 seconds in TodoActivity.java

