package br.com.sw2you.realmeet.integration;

import br.com.sw2you.realmeet.api.facade.AllocationApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import static br.com.sw2you.realmeet.utils.TestDataCreator.newAllocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void testCreateAllocationValidationError() {
        var room = roomRepository.saveAndFlush(newRoomBuilder().build());
        var createAllocationDTO = newCreateAllocationDTO()
                .roomId(room.getId())
                .subject(null);

        assertThrows(
                HttpClientErrorException.UnprocessableEntity.class,
                () -> api.createAllocation(newCreateAllocationDTO().subject(null))
        );
    }

    @Test
    void testCreateAllocationWhenRoomDoesNotExist() {
        assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> api.createAllocation(newCreateAllocationDTO().subject(null))
        );
    }

    @Test
    void testDeleteAllocationSuccess() {
        var room = roomRepository.saveAndFlush(newRoomBuilder().build());
        var allocation = allocationRepository.saveAndFlush(newAllocationBuilder(room).build());

        api.deleteAllocation(allocation.getId());
        assertFalse(allocationRepository.findById(allocation.getId()).isPresent());
    }

    @Test
    void testDeleteAllocationInThePast() {
        var room = roomRepository.saveAndFlush(newRoomBuilder().build());
        var allocation = allocationRepository.saveAndFlush(
                newAllocationBuilder(room)
                        .startAt(DateUtils.now().minusDays(1))
                        .endAt(DateUtils.now().minusDays(1).plusHours(1))
                        .build()
        );

        assertThrows(HttpClientErrorException.UnprocessableEntity.class, () -> api.deleteAllocation(allocation.getId()));
    }

    @Test
    void testDeleteAllocationDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.deleteAllocation(1L));
    }
}