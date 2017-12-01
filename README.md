# Shuttle Xpress Driver for android

<img align="right" src="icon.png" height="210">

This is an Android library which can be used with a with an application to take input from a connected [Contour design Shuttle Xpress](https://contour-design.co.uk/product/shuttlexpress/).

The library has been used in my [Headunit Controller App](https://github.com/freshollie/AndroidHeadunitController), but other example usage can be found in the example app.

## Usage

### Connecting

```java
ShuttleXpressConnection connection = new ShuttleXpressConnection(context);
ShuttleXpressDevice device = connetion.getDevice();

// Show a notification to show the device is connected
connection.setShowNotifications(wantNotification);

// Request to open a connection
connection.open();
```
     
### Interface

```java
// UI Updates can be performed through the listeners

connection.registerConnectionChangeListener(new ShuttleXpressConnection.ConnectionChangeListener() {
    @Override
    public void onConnected() {
        setConnectionStatus(ShuttleXpressDevice.STATUS_CONNECTED);
    }

    @Override
    public void onDisconnected() {
        setConnectionStatus(ShuttleXpressDevice.STATUS_DISCONNECTED);
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

```gradle
compile project(":shuttle-xpress-android")
```
