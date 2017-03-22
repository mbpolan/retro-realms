package com.mbpolan.retrorealms.repositories.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Entity that describes a user in the database.
 *
 * @author mbpolan
 */
@Entity
public class UserAccount {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String sprite;
    private String direction;
    private int mapArea;
    private int x;
    private int y;
    private int speed;
    private long lastLogin;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSprite() {
        return sprite;
    }

    public String getDirection() {
        return direction;
    }

    public int getMapArea() {
        return mapArea;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    private UserAccount() {
    }
}
