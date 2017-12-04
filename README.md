<img align="right" src="library-icon.png" height="200px">

# Shuttle Xpress Driver for android

This is an Android library which can be used to incorperate user input from a [Contour design Shuttle Xpress](https://contour-design.co.uk/product/shuttlexpress/) into an Android application.

The library has been used in my [Headunit Controller App](https://github.com/freshollie/AndroidHeadunitController), but other usage can be found in the example app.

The library currently requires API 17 or higher, due to a limitation of the UsbRequests API (This may be lowered in the future)

## Usage

### Connecting

```java
ShuttleXpressConnection connection = new ShuttleXpressConnection(context);
ShuttleXpressDevice device = connection.getDevice();

// Show a notification to show the device is connected
connection.setShowNotifications(wantNotification);

// Request to open a connection
connection.open();
```
     
### Interface

```java
// UI Updates can be performed through the listeners

connection.registerConnectionChangeListener(new ShuttleXpressConnection.ConnectionStateChangeListener() {
    @Override
    public void onChange(int newState) {
        if (newState == ShuttleXpressConnection.STATE_CONNECTED) {
            ...
        }
    }
});


device.registerKeyListener(new ShuttleXpressDevice.KeyListener() {
    @Override
    public void onDown(int id) {
        if (id == ShuttleXpressDevice.KeyCodes.WHEEL_LEFT) {
            // Wheel has clicked left
        } else if (id == ShuttleXpressDevice.KeyCodes.WHEEL_RIGHT) {
            // Wheel has clicked right
        }
    }

    @Override
    public void onUp(int id) {
        if (id == ShuttleXpressDevice.KeyCodes.BUTTON_0) {
            // Button 0 has finished being pressed
        }
    }
});
```
   
Or read values manually:

```java
for (int i = 0; i < 5; i++) {
    Log.v(TAG, String.valueOf(device.getButtonState(i)));
}

Log.v(TAG, String.valueOf(device.getRingState()));
Log.v(TAG, String.valueOf(device.getRingPosition()));
Log.v(TAG, String.valueOf(device.getWheelPosition()));
```
    
## Adding Shuttle Xpress your Android Studio project

### Easier method

1. `git clone https://github.com/freshollie/shuttle-xpress-android.git` wherever you wish
1. Open your android studio project
1. File -> New -> Import module: `path/to/shuttle-xpress-android/shuttle-xpress-library`

Done. However you will need to manually to update the sourcecode in your projects
directory when this library updates.


### Better method

1. Go to your project root directory
1. `git submodule add https://github.com/freshollie/shuttle-xpress-android.git`
1. `git sudmodule update shuttle-xpress-android`
1. Open `settings.gradle`
1. Add `include ':shuttle-xpress-library'`
1. And `project(':shuttle-xpress-library').projectDir = new File('shuttle-xpress-android/shuttle-xpress-library')`
1. Finally open `app/build.gradle`
1. And add `compile project(':shuttle-xpress-library')` to the build dependencies

Brilliant, now your app repository contains this library as a submodule, which can easily be update with `git pull`
inside from inside the module directory

To sync the submodule after a clone

`git submodule init; git submodule update shuttle-xpress-android`
