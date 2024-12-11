package com.dreamgames.backendengineeringcasestudy.model;

import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer coins = 2000;

    @Enumerated(EnumType.STRING)
    @Column(name = "ab_test_group", nullable = false)
    private ABTestGroup abTestGroup;

    @Column
    private Integer heliumCount = 0;

    @Column(nullable = true)
    private Long partnerID;

    // Enum for A/B Test Groups
    public enum ABTestGroup {
        GroupA,
        GroupB
    }

    // Constructors
    public User() {
        this.abTestGroup = new Random().nextBoolean()
                ? User.ABTestGroup.GroupA
                : User.ABTestGroup.GroupB;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public ABTestGroup getAbTestGroup() {
        return abTestGroup;
    }

    public void setABTestGroup(ABTestGroup abTestGroup) {
        this.abTestGroup = abTestGroup;
    }

    public Integer getHeliumCount() {
        return heliumCount;
    }

    public void setHeliumCount(Integer heliumCount) {
        this.heliumCount = heliumCount;
    }

    public Long getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(Long partnerID) {
        this.partnerID = partnerID;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", level=" + level +
                ", coins=" + coins +
                ", abTestGroup=" + abTestGroup +
                ", heliumCount=" + heliumCount +
                ", partnerID='" + partnerID +
                '}';
    }
}