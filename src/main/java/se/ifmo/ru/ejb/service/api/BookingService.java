package se.ifmo.ru.ejb.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ejb.Remote;
import jakarta.ws.rs.core.Response;
import se.ifmo.ru.ejb.service.model.Ticket;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
@Remote
public interface BookingService {
    String copyTicketWithVipAndDoublePrice(int id) throws NoSuchAlgorithmException, KeyManagementException, JsonProcessingException;
    boolean deleteAllTicketsWithNameAndCoordinates(String name, int x, int y) throws CertificateException, NoSuchAlgorithmException, KeyManagementException;
}
