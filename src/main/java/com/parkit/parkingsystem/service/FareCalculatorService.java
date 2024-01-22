package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {
	public void calculateFare(Ticket ticket){
	       calculateFare(ticket,true);
	}
    public void calculateFare(Ticket ticket,boolean discount) {

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        long inHour =  ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        double duration = (double) (outHour - inHour) /(60*60*1000);

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
        applyDiscount( ticket, discount);
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
    
      private void applyDiscount(Ticket ticket, boolean discount) {
            if (discount) {
                ticket.setPrice(0.95 * ticket.getPrice());    
            }
    }   
}

