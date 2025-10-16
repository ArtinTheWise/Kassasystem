package org.example.Membership;

public class Points {

    private long amountOfPoints;

    public Points() {
        this.amountOfPoints = 0;
    }

    public Points(long amountOfPoints) {
        this.amountOfPoints = amountOfPoints;
    }

    public void add(long points) {
        if(points < 0) throw new IllegalArgumentException("Points cannot be negative");
        this.amountOfPoints += points;
    }

    public void add(Points points) {
        add(points.amountOfPoints);
    }

    public void subtract(long points) {
        if(points < 0) throw new IllegalArgumentException("Points cannot be negative");
        if(points > amountOfPoints) throw new IllegalArgumentException("exceeds amount of total points");
        this.amountOfPoints -= points;
    }

    public void subtract(Points points) {
        subtract(points.amountOfPoints);
    }

    public long getAmount() {
        return amountOfPoints;
    }
}
