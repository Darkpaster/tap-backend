package com.human.tapMMO.dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.human.tapMMO.runtime.game.Position;
import com.human.tapMMO.util.Util;
import lombok.Data;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Data
public class MobDTO {
    protected enum States {
        WANDERING,
        FLEEING,
        CHASING,
        SLEEPING,
    }

    private long id;
    protected float x;
    protected float y;
    protected byte speed = 2;
    protected int health = 100;
    protected String renderState = "idle";

    protected Position target = null;

    @JsonIgnore
    protected short agroRadius = 100;
    @JsonIgnore
    protected States state = States.WANDERING;
    @JsonIgnore
    private Instant timeToWalk = Instant.now().plusSeconds(Util.randomInt(3));
    @JsonIgnore
    private byte randomDirection = 0;

    private void moveTowardsTarget() {
        if (this.target == null) {
            this.state = States.WANDERING;
            return;
        }
        final float distance = getDistance(this.getX(), this.getY(), this.target.getX(), this.target.getY());
        if (distance > 200) {
            this.state = States.WANDERING;
            this.target = null;
        } else if (distance > 16) {
            this.setX(this.getX() + (this.target.getX() - this.getX()) / distance * speed);
            this.setY(this.getY() + (this.target.getY() - this.getY()) / distance * speed);
        }
    }

    private void setNewTargetPoint(Collection<Position> players) {
        for (Position player: players) {
            if (getDistance(player.getX(), player.getY(), x, y) < this.agroRadius) {
                this.target = player;
                this.target.setX(player.getX());
                this.target.setY(player.getY());
                this.state = States.CHASING;
                this.renderState = "run";
                return;
            }
        }
    }

    private void wander() {
        if (this.timeToWalk.isBefore(Instant.now())) {
            if (Objects.equals(this.renderState, "idle")) {
                this.renderState = "walk";
                this.randomDirection = (byte) Util.randomInt(3);
            } else {
                this.renderState = "idle";
            }
            this.timeToWalk = Instant.now().plusSeconds(Util.randomInt(2, 5));
        }
        if (Objects.equals(this.renderState, "walk")) {
            switch (randomDirection) {
                case 0:
                    this.x -= (float) speed / 2;
                    break;
                case 1:
                    this.x += (float) speed / 2;
                    break;
                case 2:
                    this.y -= (float) speed / 2;
                    break;
                case 3:
                    this.y += (float) speed / 2;
            }
        }
    }

    private void moveAwayFromTarget() {
        final float distance = getDistance(this.getX(), this.getY(), this.target.getX(), this.target.getY());
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

    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private float getDistanceX(float x1, float x2) {
        return Math.abs(Math.abs(x1) - Math.abs(x2));
    }

    private float getDistanceY(float y1, float y2) {
        return Math.abs(Math.abs(y1) - Math.abs(y2));
    }

    public boolean dealDamage(int value) {
        health -= value;
        if (health <= 0) {
            this.renderState = "death";
            return true;
        }
        return false;
    }

}
