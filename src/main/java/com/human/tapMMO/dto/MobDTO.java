package com.human.tapMMO.dto;


import com.human.tapMMO.model.Position;
import com.human.tapMMO.model.tables.Mob;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Data
public class MobDTO {
    protected enum States {
        WANDERING,
        FLEEING,
        CHASING,
    }

    private long id;
    protected int x;
    protected int y;
    protected int speed = 5;
    protected int health = 100;
    protected String renderState = "idle";

    protected Position target = null;

    protected int agroRadius = 200;

    protected States state = States.WANDERING;

    private void moveTowardsTarget() {
        if (target == null) {
            this.state = States.WANDERING;
            return;
        }
        final int distance = getDistance(this.getX(), this.getY(), this.target.getX(), this.target.getY());
        if (distance > 500) {
            this.state = States.WANDERING;
            this.target = null;
        } else if (distance > 20) {
            this.setX(this.getX() + (this.target.getX() - this.getX()) / distance * speed);
            this.setY(this.getY() + (this.target.getY() - this.getY()) / distance * speed);
        }
    }

    private void setNewTargetPoint(Collection<Position> players) {
        for (Position player: players) {
            if (getDistance(player.getX(), player.getY(), x, y) < this.agroRadius) {
                this.target = player;
                System.out.println(this.target.getX());
                System.out.println(this.target.getY());
                this.state = States.CHASING;
                return;
            }
        }
    }

    private void wander() {
        this.setX((int) (this.getX() + (Math.random() * 10 - 5)));
        this.setY((int) (this.getY() + (Math.random() * 10 - 5)));
    }

    private void moveAwayFromTarget() {
        final int distance = getDistance(this.getX(), this.getY(), this.target.getX(), this.target.getY());
        if (distance < 500) {
//            this.setX(this.getX() - (this.target.getX() - this.getX()) / distance * speed);
//            this.setY(this.getY() - (this.target.getY() - this.getY()) / distance * speed);
        }
    }

    public MobDTO update(HashMap<Long, Position> players) {
        final var playerList = players.values();
        if (this.state == States.WANDERING) {
            wander();
            setNewTargetPoint(playerList);
        } else if (this.state == States.FLEEING) {
            moveAwayFromTarget();
        } else {
            moveTowardsTarget();
        }
        return this;
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}
