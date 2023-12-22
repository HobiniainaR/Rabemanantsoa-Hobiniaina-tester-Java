package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if (ticket.getInTime() == null || ticket.getOutTime() == null) {
            throw new IllegalArgumentException("InTime or OutTime is missing");
        }

        Date inTime = ticket.getInTime();
        Date outTime = ticket.getOutTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationInMillis = outTime.getTime() - inTime.getTime();
        double duration = durationInMillis / (60.0 * 60.0 * 1000.0);

        if (duration < 0) {
            throw new IllegalArgumentException("OutTime is before InTime");
        }

        // Calculate fare based on parking type
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                calculateCarFare(ticket, duration);
                break;
            case BIKE:
                calculateBikeFare(ticket, duration);
                break;
            default:
                throw new IllegalArgumentException("Unknown parking type");
        }
    }

    private void calculateCarFare(Ticket ticket, double duration) {
        if (duration < 0.5) {
            ticket.setPrice(0);
        } else if (duration <= 1) {
            ticket.setPrice(duration*Fare.CAR_RATE_PER_HOUR);
        } else {
            ticket.setPrice(duration*Fare.CAR_RATE_PER_HOUR);
        }
    }

    private void calculateBikeFare(Ticket ticket, double duration) {
        if (duration < 0.5) {
            ticket.setPrice(0);
        } else if (duration <= 1) {
            ticket.setPrice(duration*Fare.BIKE_RATE_PER_HOUR);
        } else {
            ticket.setPrice(duration*Fare.BIKE_RATE_PER_HOUR);
        }
    }
}
