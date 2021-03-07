package com.nplekhanov.swing;


public interface MouseState {

    Vector2D getPosition();

    boolean wasJustPressed(MouseButton button);

    boolean wasJustReleased(MouseButton button);

    boolean isPressed(MouseButton button);

    float getWheelDelta();
}
