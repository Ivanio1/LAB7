package data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private long x;
    private long y;

    public Coordinates(long x, long y) {
        this.x = x;
        this.y = y;
    }
    public Coordinates(){}

    /**
     * @return X-coordinate.
     */
    public long getX() {
        return x;
    }

    /**
     * @return Y-coordinate.
     */
    public long getY() {
        return y;
    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
