package se.ifmo.ru.ejb.external.client;

import jakarta.ejb.Stateless;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.jboss.ejb3.annotation.Pool;
import org.w3c.dom.Node;
import se.ifmo.ru.ejb.external.SSLValidation;
import se.ifmo.ru.ejb.external.model.RestClientTicket;
import se.ifmo.ru.ejb.external.model.TicketListGetResponseDto;
import se.ifmo.ru.ejb.external.model.TicketAddOrUpdateRequestDto;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Stateless
@Slf4j
@Pool("slsb-strict-max-pool")
public class CatalogRestClient {
    private Client client;
    private final String serviceUrl = "https://service1:6161/service";



    public RestClientTicket getTicketById(int id) throws NoSuchAlgorithmException, KeyManagementException {
        String url = serviceUrl + "/tickets/" + id;
        final SSLContext context = SSLContext.getInstance("TLSv1.2");
        final TrustManager[] trustManagerArray = {new NullX509TrustManager()};
        context.init(null, trustManagerArray, null);

        try {
            client = ClientBuilder.newBuilder()
                    .hostnameVerifier(new NullHostnameVerifier())
                    .sslContext(context)
                    .build();

            Response response = client.target(url).request().get();

            RestClientTicket ticket = response.readEntity(RestClientTicket.class);
            System.out.println(ticket);
            client.close();
            return ticket;

        } catch (ProcessingException e) {
            log.error("Aboba: ", e);
            return null;
        }


    }


    public List<RestClientTicket> getAllTickets() throws CertificateException, NoSuchAlgorithmException, KeyManagementException {
        String url = serviceUrl + "/tickets";
        final SSLContext context = SSLContext.getInstance("TLSv1.2");
        final TrustManager[] trustManagerArray = {new NullX509TrustManager()};
        context.init(null, trustManagerArray, null);
        try {
            client =  ClientBuilder.newBuilder()
                    .hostnameVerifier(new NullHostnameVerifier())
                    .sslContext(context)
                    .build();
            Response response = client.target(url).request(MediaType.APPLICATION_XML).get();
            List<RestClientTicket> flats = Arrays.asList(response.readEntity(RestClientTicket[].class));

            client.close();

            return flats;
        } catch (ProcessingException e) {
            log.error("Aboba: ", e);
            return null;
        }
    }

    public RestClientTicket addTicket(TicketAddOrUpdateRequestDto requestDto) throws NoSuchAlgorithmException, KeyManagementException {
        String url = serviceUrl + "/tickets";
        final SSLContext context = SSLContext.getInstance("TLSv1.2");
        final TrustManager[] trustManagerArray = {new NullX509TrustManager()};
        System.out.println(requestDto);
        context.init(null, trustManagerArray, null);
        try {

            client =  ClientBuilder.newBuilder()
                    .hostnameVerifier(new NullHostnameVerifier())
                    .sslContext(context)
                    .build();
            Response response = client.target(url).request(MediaType.APPLICATION_XML_TYPE).post(Entity.xml(
                    requestDto
            ));
            RestClientTicket ticket = response.readEntity(RestClientTicket.class);

            client.close();
            return ticket;

        } catch (ProcessingException e) {
            log.error("Aboba: ", e);
            return null;
        }
    }

    public boolean deleteTicket(int id) throws NoSuchAlgorithmException, KeyManagementException {
        String url = serviceUrl + "/tickets/" + id;
        final SSLContext context = SSLContext.getInstance("TLSv1.2");
        final TrustManager[] trustManagerArray = {new NullX509TrustManager()};
        context.init(null, trustManagerArray, null);
        try {
            client =  ClientBuilder.newBuilder()
                    .hostnameVerifier(new NullHostnameVerifier())
                    .sslContext(context)
                    .build();
            Response response = client.target(url).request(MediaType.APPLICATION_XML_TYPE).delete();

            return response.getStatus() == Response.Status.NO_CONTENT.getStatusCode();

        } catch (ProcessingException e) {
            log.error("Aboba: ", e);
            return false;
        }

    }
    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static class NullX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
