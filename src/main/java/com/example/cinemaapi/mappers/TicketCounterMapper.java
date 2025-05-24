package com.example.cinemaapi.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.cinemaapi.dto.ClientDTO;
import com.example.cinemaapi.dto.TicketCounterDTO;
import com.example.cinemaapi.model.Client;
import com.example.cinemaapi.model.TicketCounter;

public abstract class TicketCounterMapper {

    public static ClientDTO toClientDTO(Client client) {
        if (client == null)
            return null;
        return new ClientDTO(client.getId(), client.getName(), client.getType());
    }

    public static List<ClientDTO> toClientDTOList(List<Client> clients) {
        return clients.stream()
                .map(TicketCounterMapper::toClientDTO)
                .collect(Collectors.toList());
    }

    public static TicketCounterDTO toTicketCounterDTO(TicketCounter counter) {
        return new TicketCounterDTO(
                counter.getId(),
                !counter.isActive(),
                toClientDTO(counter.getCurrentClient()),
                toClientDTOList(counter.getQueue()));
    }

}
