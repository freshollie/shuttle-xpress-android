<img align="right" src="icon.png" height="200px">

# Shuttle Xpress Driver for android

This is an Android library which can be used to incorperate user input from a [Contour design Shuttle Xpress](https://contour-design.co.uk/product/shuttlexpress/) into an Android application.

The library has been used in my [Headunit Controller App](https://github.com/freshollie/AndroidHeadunitController), but other usage can be found in the example app.

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
    public void onChange(newState) {
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
    
## Adding to project
1. In your android project root directory

```
git submodule add https://github.com/freshollie/shuttle-xpress-android.git`
git sudmodule update shuttle-xpress-android
```

2. In your build.gradle

```gradle
compile project(":shuttle-xpress-android")
```
