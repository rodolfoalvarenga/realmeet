package br.com.sw2you.realmeet.integration;

import br.com.sw2you.realmeet.api.facade.AllocationApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllocationApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AllocationApi api;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(api.getApiClient(), "/v1");
    }

    @Test
    void testCreateAllocationSuccess() {
        var room = roomRepository.saveAndFlush(newRoomBuilder().build());
        var createAllocationDTO = newCreateAllocationDTO().roomId(room.getId());
        var allocationDTO = api.createAllocation(createAllocationDTO);

        assertNotNull(allocationDTO.getId());
        assertEquals(room.getId(), allocationDTO.getRoomId());
        assertEquals(createAllocationDTO.getSubject(), allocationDTO.getSubject());
        assertEquals(createAllocationDTO.getEmployeeName(), allocationDTO.getEmployeeName());
        assertEquals(createAllocationDTO.getEmployeeEmail(), allocationDTO.getEmployeeEmail());
        assertTrue(createAllocationDTO.getStartAt().isEqual(allocationDTO.getStartAt()));
        assertTrue(createAllocationDTO.getEndAt().isEqual(allocationDTO.getEndAt()));
    }
}