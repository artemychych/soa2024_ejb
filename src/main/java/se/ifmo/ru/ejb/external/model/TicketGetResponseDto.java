package se.ifmo.ru.ejb.external.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType
@XmlRootElement(name = "Ticket")
public class TicketGetResponseDto {
    private Integer id;
    private String name;
    private TicketCoordinatesGetResponseDto coordinates;
    private String creationDate;
    private Float price;
    private String type;
    private TicketPersonGetResponseDto person;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType
    @XmlRootElement(name = "Coordinates")
    public static class TicketCoordinatesGetResponseDto {
        private Integer x;
        private int y;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType
    @XmlRootElement(name = "Person")
    public static class TicketPersonGetResponseDto {
        private Long weight; //Поле может быть null, Значение поля должно быть больше 0
        private String hairColor; //Поле не может быть null
        private TicketPersonLocationGetResponseDto location; //Поле не может быть null
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType
    @XmlRootElement(name = "Location")
    public static class TicketPersonLocationGetResponseDto {
        private Float x; //Поле не может быть null
        private long y;
        private double z;
    }


}
