# Android Shuttle Xpress Driver Libary

Library which is used to allow for input from a contour design shuttle xpress in an android app.

See usage in example app.

## Usage

### Connecting

    Driver driver = new Driver(context);
    ShuttleXpressDevice device = driver.getDevice();
    // Creates a notification and starts the connection to the
    // ShuttleXpress Device;
    driver.start(); 
     
### Interface

```java
// This click listener is thread safe. UI Updates can be performed through the listener

device.registerKeyListener(new ShuttleXpressDevice.KeyListener() {
    @Override
    public void onDown(final int id) {
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
    Log.V(TAG, String.valueOf(device.getButtonState(i)));
}

Log.V(TAG, String.valueOf(device.getRingState()));
Log.V(TAG, String.valueOf(device.getRingPosition()));
Log.V(TAG, String.valueOf(device.getWheelPosition()));
```
    
    

## Adding to project

	compile project(":ShuttleXpressDriver-Android")
