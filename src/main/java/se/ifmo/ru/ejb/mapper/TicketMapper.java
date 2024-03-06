package se.ifmo.ru.ejb.mapper;

import org.mapstruct.Mapper;
import se.ifmo.ru.ejb.external.model.RestClientTicket;
import se.ifmo.ru.ejb.service.model.Color;
import se.ifmo.ru.ejb.service.model.Ticket;
import se.ifmo.ru.ejb.service.model.TicketType;
import se.ifmo.ru.ejb.external.model.TicketGetResponseDto;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "jakarta")
public interface TicketMapper {
    TicketGetResponseDto toDto(Ticket source);
    List<TicketGetResponseDto> toGetResponseDtoList(List<Ticket> source);
    Ticket fromRestClient(RestClientTicket restClientFlat);
    List<Ticket> fromRestClient(List<RestClientTicket> restClientFlat);

    default String fromTicketType(TicketType type) {
        return type.toString();
    }

    default String fromHairColor(Color color) {
        return Objects.requireNonNullElse(color, Color.NONE).toString();
    }

    default TicketType fromStringTicketType(String type){
        return TicketType.fromValue(type);
    }

    default Color fromStringColor(String color){
        return Color.fromValue(color);
    }
}

