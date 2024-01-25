package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;


    @Mock(lenient = true)
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(inputReaderUtil.readSelection()).thenReturn(1);
        dataBasePrepareService.clearDataBaseEntries();

    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //THEN
        parkingService.processIncomingVehicle();
        //WHEN
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());

        assertEquals(1, ticket.getId());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertNotNull(ticket.getInTime());
        assertEquals(1, ticketDAO.getNbTicket("ABCDEF"));
        assertFalse(ticket.getParkingSpot().isAvailable());

    }


    @Test
    public void testParkingLotExit() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //THEN
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTimeTicket = new Date();
        inTimeTicket.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTimeTicketOne = new Date();


        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(inTimeTicket);
        ticketDAO.saveTicket(ticket);
         parkingService.processExitingVehicle();

            //WHEN
            ticket = ticketDAO.getTicket("ABCDEF");
            assertNotNull(ticket.getOutTime());
            assertEquals(1.5, ticket.getPrice(),0.001);

    }

    @Test
    public void testParkingLotExitRecurringUser() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        Ticket ticketOne = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTimeTicketOne = new Date();
        inTimeTicketOne.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTimeTicketOne = new Date();
        outTimeTicketOne.setTime(System.currentTimeMillis());

        ticketOne.setParkingSpot(parkingSpot);
        ticketOne.setVehicleRegNumber("ABCDEF");
        ticketOne.setInTime(inTimeTicketOne);
        ticketOne.setOutTime(outTimeTicketOne);
        ticketDAO.saveTicket(ticketOne);

        //THEN
        parkingService.processIncomingVehicle();
        Ticket ticketTwo = ticketDAO.getTicket("ABCDEF");
        Date inTimeTicketTwo = new Date();
        inTimeTicketTwo.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTimeTicketTwo = new Date();
        outTimeTicketTwo.setTime(System.currentTimeMillis());

        ticketTwo.setInTime(inTimeTicketTwo);
        ticketTwo.setOutTime(outTimeTicketTwo);
        ticketDAO.updateTicket(ticketTwo);
            parkingService.processExitingVehicle();
        //WHEN
            ticketTwo = ticketDAO.getTicket("ABCDEF");
            assertEquals(2, ticketDAO.getNbTicket("ABCDEF"));
            assertEquals(0.95 * Fare.CAR_RATE_PER_HOUR, ticketTwo.getPrice(),0.001);

    }
}
    
