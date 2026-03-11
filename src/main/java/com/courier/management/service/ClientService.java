package com.courier.management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courier.management.dto.ClientDTO;
import com.courier.management.entity.Client;
import com.courier.management.entity.State;
import com.courier.management.repository.ClientRepository;
import com.courier.management.repository.StateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final StateRepository stateRepository;

    // -------- GET CLIENT --------
    public ClientDTO getClient(Integer clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return mapToDTO(client);
    }

    // -------- UPDATE CLIENT --------
    public ClientDTO updateClient(Integer clientId, ClientDTO dto) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        State state = stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        client.setClientName(dto.getClientName());
        client.setClientAdd(dto.getClientAdd());
        client.setClientPin(dto.getClientPin());
        client.setClientGstin(dto.getClientGstin());
        client.setClientIsgst(dto.getClientIsgst());
        client.setTaxRate(dto.getTaxRate());
        client.setState(state);

        client.setTagLine(dto.getTagLine());
        client.setSacCode(dto.getSacCode());
        client.setContNo(dto.getContNo());
        client.setContMail(dto.getContMail());

        client.setBName(dto.getBName());
        client.setBAcc(dto.getBAcc());
        client.setBIfsc(dto.getBIfsc());

        client.setCompPan(dto.getCompPan());
        client.setCompMsme(dto.getCompMsme());

        client.setCondition1(dto.getCondition1());
        client.setCondition2(dto.getCondition2());
        client.setCondition3(dto.getCondition3());
        client.setCondition4(dto.getCondition4());
        client.setCondition5(dto.getCondition5());

        Client updatedClient = clientRepository.save(client);

        return mapToDTO(updatedClient);
    }

    // -------- ENTITY → DTO --------
    private ClientDTO mapToDTO(Client client) {

        ClientDTO dto = new ClientDTO();

        dto.setClientId(client.getClientId());
        dto.setClientName(client.getClientName());
        dto.setClientAdd(client.getClientAdd());
        dto.setClientPin(client.getClientPin());
        dto.setClientGstin(client.getClientGstin());
        dto.setClientIsgst(client.getClientIsgst());
        dto.setClientCreate(client.getClientCreate());
        dto.setTaxRate(client.getTaxRate());

        dto.setStateId(client.getState().getStateCode());

        dto.setTagLine(client.getTagLine());
        dto.setSacCode(client.getSacCode());
        dto.setContNo(client.getContNo());
        dto.setContMail(client.getContMail());

        dto.setBName(client.getBName());
        dto.setBAcc(client.getBAcc());
        dto.setBIfsc(client.getBIfsc());

        dto.setCompPan(client.getCompPan());
        dto.setCompMsme(client.getCompMsme());

        dto.setCondition1(client.getCondition1());
        dto.setCondition2(client.getCondition2());
        dto.setCondition3(client.getCondition3());
        dto.setCondition4(client.getCondition4());
        dto.setCondition5(client.getCondition5());

        return dto;
    }
}