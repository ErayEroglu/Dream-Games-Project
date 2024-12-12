package com.dreamgames.backendengineeringcasestudy.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "partnerships")
public class Partnership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnershipStatus status;

    public enum PartnershipStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Column
    private int balloonProgress;

    private int inflationThreshold;

    public Partnership() {
        if (!isValidTime()) {
            throw new IllegalArgumentException("Partnerships can only exist during sessions. Sessions start at 08:00 and end at 22:00 UTC");
        }
    }

    public Partnership(User sender, User receiver, PartnershipStatus status) {
        if (!isValidTime()) {
            throw new IllegalArgumentException("Partnerships can only exist during sessions. Sessions start at 08:00 and end at 22:00 UTC");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public PartnershipStatus getStatus() {
        return status;
    }

    public void setStatus(PartnershipStatus status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Partnership{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status=" + status +
                ", balloonProgress=" + balloonProgress +
                ", inflationThreshold=" + inflationThreshold +
                '}';
    }

    public void updateStatus(PartnershipStatus status) {
        this.status = status;
        if (status == PartnershipStatus.ACCEPTED) {
            this.balloonProgress = 0;
            this.inflationThreshold = sender.getAbTestGroup().equals(User.ABTestGroup.GroupA) ? 1000 : 1500;
        }
    }

    private boolean isValidTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int hour = now.getHour();
//        return hour >= 8 && hour <= 22;
        return true; //TODO: Remove the comments in production
    }
}