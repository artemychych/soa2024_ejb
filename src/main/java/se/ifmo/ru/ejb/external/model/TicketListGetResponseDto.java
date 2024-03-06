package se.ifmo.ru.ejb.external.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class TicketListGetResponseDto {
    @JacksonXmlProperty(localName = "Ticket")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RestClientTicket> ticketGetResponseDtos;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalCount;

    public List<RestClientTicket> getTicketGetResponseDtos () {
        return ticketGetResponseDtos;
    }

    public TicketListGetResponseDto(List<RestClientTicket> ticketGetResponseDtos, Integer page, Integer pageSize, Integer totalPages, Long totalCount){
        this.ticketGetResponseDtos = ticketGetResponseDtos;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }
}