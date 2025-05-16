package com.human.tapMMO.runtime.game.actors.mob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.human.tapMMO.dto.websocket.Position;
import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.actors.ActorRenderStates;
import com.human.tapMMO.runtime.game.actors.mob.enemy.BlueSlime;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.runtime.game.config.GameConfig;
import com.human.tapMMO.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static com.human.tapMMO.util.Util.getDistance;

@Getter
@Setter
public abstract class Mob extends Actor {
    protected MobState state;
    protected int experienceValue;
    protected short aggroRange;

    protected Instant timeToWalk = Instant.now().plusSeconds(Util.randomInt(3));
    private byte randomDirection = 0;

    public Mob() {
        this.aggroRange = GameConfig.TILE_SIZE * 3;
        this.state = MobState.IDLE;
        this.experienceValue = 1;
    }

    public abstract void attack(Actor target);


    public <T extends Actor> Mob update(HashMap<Long, T> players) {
        final var playerList = players.values();
        switch (state) {
            case MobState.IDLE:
                wander();
                setNewTargetPoint(playerList);
                break;
            case MobState.AGGRO:
                // Движение к цели и атака
                moveTowardsTarget();
                break;
            case MobState.RETURNING:
                // Возвращение на исходную позицию
                break;
            case MobState.DEAD:
                // Ничего не делать, моб мертв
                break;
            case MobState.FLEEING:
                moveAwayFromTarget();
                break;
            case MobState.SLEEPING:

                break;
        }
        return this;
    }

    @Override
    public void onDeath() {
        setState(MobState.DEAD);
        // Логика при смерти моба (выпадение добычи и т.д.)
    }


    private void moveTowardsTarget() {
        if (this.getTarget() == null) {
            this.state = MobState.IDLE;
            return;
        }
        final float distance = getDistance(this.x, this.y, this.target.getX(), this.target.getY());
        if (distance > 200) {
            this.state = MobState.IDLE;
            this.setTarget(null);
        } else if (distance > 16) {
            this.x += (this.target.getX() - this.x) / distance * this.speed;
            this.y += (this.target.getY() - this.y) / distance * this.speed;
        }
    }

    private <T extends Actor> void setNewTargetPoint(Collection<T> players) {
        for (Actor player: players) {
            if (getDistance(player.getX(), player.getY(), this.x, this.y) < this.aggroRange) {
                this.target = player;
                this.target.setX(player.getX());
                this.target.setY(player.getY());
                this.state = MobState.AGGRO;
                this.renderState = ActorRenderStates.RUN;
                return;
            }
        }
    }

    private void wander() {
        if (this.timeToWalk.isBefore(Instant.now())) {
            if (Objects.equals(this.renderState, ActorRenderStates.IDLE)) {
                this.renderState = ActorRenderStates.WALK;
                this.randomDirection = (byte) Util.randomInt(3);
            } else {
                this.renderState = ActorRenderStates.IDLE;
            }
            this.timeToWalk = Instant.now().plusSeconds(Util.randomInt(2, 5));
        }
        if (Objects.equals(this.renderState, ActorRenderStates.WALK)) {
            switch (randomDirection) {
                case 0:
                    this.x = this.x - (float) this.speed / 2;
                    break;
                case 1:
                    this.x = this.x + (float) this.speed / 2;
                    break;
                case 2:
                    this.y = this.y - (float) this.speed / 2;
                    break;
                case 3:
                    this.y = this.y + (float) this.speed / 2;
            }
        }
    }

    private void moveAwayFromTarget() {
        final float distance = getDistance(this.x, this.y, this.target.getX(), this.target.getY());
        if (distance < 500) {
            this.x -= (this.target.getX() - this.x) / distance * this.speed;
            this.y -= (this.target.getY() - this.y) / distance * this.speed;
        }
    }

    public static <T extends Mob> T createMob(String name) {
        switch (name) {
            case "blueSlime": new BlueSlime();
        }
        return null;
    }

}
