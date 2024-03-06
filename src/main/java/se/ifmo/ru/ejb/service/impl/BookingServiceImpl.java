package se.ifmo.ru.ejb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.ejb3.annotation.Pool;
import se.ifmo.ru.ejb.external.client.CatalogRestClient;
import se.ifmo.ru.ejb.external.model.RestClientTicket;
import se.ifmo.ru.ejb.external.model.TicketGetResponseDto;
import se.ifmo.ru.ejb.mapper.TicketMapper;
import se.ifmo.ru.ejb.service.api.BookingService;
import se.ifmo.ru.ejb.service.model.*;
import se.ifmo.ru.ejb.external.model.TicketAddOrUpdateRequestDto;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Stateless
@Pool("slsb-strict-max-pool")
public class BookingServiceImpl implements BookingService {
    @Inject
    private CatalogRestClient catalogRestClient;

    @Inject
    private TicketMapper ticketMapper;

    @Override
    public String copyTicketWithVipAndDoublePrice(int id) throws NoSuchAlgorithmException, KeyManagementException, JsonProcessingException {
        RestClientTicket ticket = catalogRestClient.getTicketById(id);
        Ticket newTicket = new Ticket();

        if (ticket == null) {
            throw new NotFoundException("Ticket with id " + id + " not found");
        }

        TicketAddOrUpdateRequestDto requestTicket = TicketAddOrUpdateRequestDto.builder()
                .name(ticket.getName())
                .coordinates(
                        TicketAddOrUpdateRequestDto.TicketCoordinatesAddResponseDto.builder()
                                .x(ticket.getCoordinates().getX())
                                .y(ticket.getCoordinates().getY()).build()
                )
                .price(ticket.getPrice() * 2)
                .type(TicketType.VIP.getValue())
                .person(
                        TicketAddOrUpdateRequestDto.TicketPersonAddResponseDto.builder()
                                .weight(ticket.getPerson().getWeight())
                                .hairColor(ticket.getPerson().getHairColor())
                                .location(
                                        TicketAddOrUpdateRequestDto.TicketPersonLocationAddResponseDto.builder()
                                                .x(ticket.getPerson().getLocation().getX())
                                                .y(ticket.getPerson().getLocation().getY())
                                                .z(ticket.getPerson().getLocation().getZ()).build()

                                ).build()
                ).build();

        RestClientTicket responsedTicket = catalogRestClient.addTicket(requestTicket);
        newTicket.setName(responsedTicket.getName());
        newTicket.setId(responsedTicket.getId());
        newTicket.setPrice(responsedTicket.getPrice());
        newTicket.setType(TicketType.VIP);
        newTicket.setCoordinates(new Coordinates(
                responsedTicket.getCoordinates().getX(), responsedTicket.getCoordinates().getY()
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        newTicket.setCreationDate(LocalDate.parse(responsedTicket.getCreationDate(), formatter));
        newTicket.setPerson(new Person(
                responsedTicket.getPerson().getWeight(),
                ticketMapper.fromStringColor(responsedTicket.getPerson().getHairColor()),
                new Location(
                        responsedTicket.getPerson().getLocation().getX(),
                        responsedTicket.getPerson().getLocation().getY(),
                        responsedTicket.getPerson().getLocation().getZ()
                        )
        ));

        return marshalAnswer(ticketMapper.toDto(newTicket));
    }

    @Override
    public boolean  deleteAllTicketsWithNameAndCoordinates(String name, int x, int y) throws CertificateException, NoSuchAlgorithmException, KeyManagementException {
        List<Ticket> tickets = ticketMapper.fromRestClient(catalogRestClient.getAllTickets());

        if (CollectionUtils.isNotEmpty(tickets)){
            for (Ticket i:
                 tickets) {
                if (i.getName().equals(name) && i.getCoordinates().getX() == x && i.getCoordinates().getY() == y) {
                    boolean response = catalogRestClient.deleteTicket(i.getId());
                    if (!response) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            throw new NotFoundException("Not found list of Tickets!");
        }
    }

    private String marshalAnswer(TicketGetResponseDto ticket) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ticket);
    }
}
