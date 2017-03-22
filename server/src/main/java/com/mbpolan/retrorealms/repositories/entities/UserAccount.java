package com.mbpolan.retrorealms.repositories.entities;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Entity that describes a user in the database.
 *
 * @author mbpolan
 */
@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SPRITE")
    private String sprite;

    @Column(name = "DIRECTION")
    private String direction;

    @Column(name = "MAP_AREA")
    private int mapArea;

    @Column(name = "X")
    private int x;

    @Column(name = "Y")
    private int y;

    @Column(name = "SPEED")
    private int speed;

    @Column(name = "LAST_LOGIN")
    private Timestamp lastLogin;

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

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    private UserAccount() {
    }
}
