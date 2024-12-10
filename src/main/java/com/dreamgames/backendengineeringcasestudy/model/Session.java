package com.dreamgames.backendengineeringcasestudy.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partnership_id", nullable = false)
    private Partnership partnership;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int balloonProgress = 0;

    private int inflationThreshold;

    public Session() {
        if (!isValidTime()) {
            throw new IllegalArgumentException("Sessions can only be started between 08:00 and 22:00 UTC");
        }
    }

    public Session(Partnership partnership, boolean active) {
        if (!isValidTime()) {
            throw new IllegalArgumentException("Sessions can only be started between 08:00 and 22:00 UTC");
        }
        this.partnership = partnership;
        this.active = active;
        this.inflationThreshold = partnership.getSender().getAbTestGroup().equals(User.ABTestGroup.GroupA) ? 1000 : 1500;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partnership getPartnership() {
        return partnership;
    }

    public void setPartnership(Partnership partnership) {
        this.partnership = partnership;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBalloonProgress() {
        return balloonProgress;
    }

    public void setBalloonProgress(int balloonProgress) {
        this.balloonProgress = balloonProgress;
    }

    public int getInflationThreshold() {
        return inflationThreshold;
    }

    public void setInflationThreshold(int inflationThreshold) {
        this.inflationThreshold = inflationThreshold;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", partnership=" + partnership +
                ", active=" + active +
                ", balloonProgress=" + balloonProgress +
                ", inflationThreshold=" + inflationThreshold +
                '}';
    }

    private boolean isValidTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int hour = now.getHour();
        //return hour >= 8 && hour <= 22;
        return true; //TODO: Remove this line on production
    }
}