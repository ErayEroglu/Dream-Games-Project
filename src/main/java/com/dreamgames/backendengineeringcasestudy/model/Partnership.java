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

    public Partnership() {
        if (!isValidTime()) {
            throw new IllegalArgumentException("Partnerships can only exist during sessions. And sessions can only be started between 08:00 and 22:00 UTC");
        }
    }

    public Partnership(User sender, User receiver, PartnershipStatus status) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (!isValidTime()) {
            throw new IllegalArgumentException("Partnerships can only exist during sessions. And sessions can only be started between 08:00 and 22:00 UTC");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public enum PartnershipStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public PartnershipStatus getStatus() {
        return status;
    }

    public void setStatus(PartnershipStatus status) {
        this.status = status;
    }

    private boolean isValidTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int hour = now.getHour();
//        return hour >= 8 && hour <= 22;
        return true; //TODO: Remove the comments in production
    }

    @Override
    public String toString() {
        return "Partnership{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status=" + status +
                '}';
    }
}