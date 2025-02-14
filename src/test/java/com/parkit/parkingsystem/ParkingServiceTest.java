package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    private static Ticket ticket;
    private static ParkingSpot parkingSpot;
    @BeforeEach
    private void setUpPerTest() {
    	 parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
         ticket = new Ticket();
         ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
         ticket.setParkingSpot(parkingSpot);
         ticket.setVehicleRegNumber("ABCDEF");
         parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
     }

     @Test
     public void processExitingVehicleTest() throws Exception {
         //GIVEN
         
             when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
             when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
             when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
             when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        
         
         //WHEN
         parkingService.processExitingVehicle();
         //THEN
         verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
         verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
         verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
     }
     
     @Test
     public void processIncomingVehicleTest() throws Exception {
         //GIVEN
         
             when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
             when(inputReaderUtil.readSelection()).thenReturn(1);
             when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
             when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
             when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        
         
         //WHEN
         parkingService.processIncomingVehicle();
         //THEN
         verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
         verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
         verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
     }
   
     @Test
     public void processExitingVehicleTestUnableUpdate() throws Exception {
    	//GIVEN
         
        	 when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        	 when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
             when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
         
         
         //WHEN
         parkingService.processExitingVehicle();
         //THEN
         verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
         verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
     }
     
     @Test 
     public void testGetNextParkingNumberIfAvailable() {
    	 //GIVEN
    	 
    		 when(inputReaderUtil.readSelection()).thenReturn(1);
             when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
         
    	 
    	 //WHEN
    	 ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
    	 //THEN
    	 assertNotNull(parkingSpot);
    	 assertEquals(1,parkingSpot.getId());
    	 
     }
     
     @Test
     public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	 //GIVEN
    	
             when(inputReaderUtil.readSelection()).thenReturn(1);
             when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
         
    	 //WHEN
    	 ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
    	 //THEN
    	 assertNull(parkingSpot);
     } 
     
     @Test
     public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgum() {
    	 //GIVEN
    	
             when(inputReaderUtil.readSelection()).thenReturn(3);
        
         
    	 //WHEN
    	 ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
    	 //THEN
    	 assertNull(parkingSpot);
     }
}
     


