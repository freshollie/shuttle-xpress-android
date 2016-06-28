# Shuttle Xpress Driver for Android
B4A app designed to act as an android driver for the Countor Design Shuttle Xpress USB input

This app has to be started and will not start when the device is connected. Best way to do this is with

    am startservice com.freshollie.shuttlexpress/.ShuttleExpressService

Currently because I have not written a UI the commands for each button are hard coded. 

**From the left:**

* First Button [Enter] or [Hold (300ms or more) Launch google play music]

* Second Button [Launch com.freshollie.radioapp (DAB Radio app)]

* Third Button [Launch pocketcasts]

* Forth Button [Launch Maps] or [Hold (3000ms or more) Launch maps driving mode]

* Fifth Button [Home] or [Hold (1000ms) Launch voice control]

* Jog wheel clockwise [TAB]

* Jog wheel anticlockwise [Up Arrow]
 
* Ring clockwise all the way [Music track next]

* Ring anticlockwise all the way [Music track previous] or [Hold (300ms or more) Back button]

If you do not have these apps the driver will still function.

The programming is pretty messy and could be cleaned up, the app was designed for my use only, but I thought I would give it out just in case anyone had an interest in it.
