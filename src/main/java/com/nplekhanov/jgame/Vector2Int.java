package com.nplekhanov.jgame;

public class Vector2Int implements RVector2Int {
    public int x;
    public int y;

    public Vector2Int() {
    }

    public Vector2Int(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public void add(final RVector2Int o) {
        x += o.getX();
        y += o.getY();
    }

    public void set(final RVector2Int o) {
        x = o.getX();
        y = o.getY();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Vector2Int that = (Vector2Int) o;
        return x == that.x &&
            y == that.y;
    }

    @Override
    public String toString() {
        return "Vector2Int{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }
}
