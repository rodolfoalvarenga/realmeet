package br.com.sw2you.realmeet.unit;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.BELOW_MIN_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DUPLICATED;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_MAX_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.MISSING;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS_MAX_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS_MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class RoomValidatorUnitTest extends BaseUnitTest {

    private RoomValidator victim;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setupEach() {
        victim = new RoomValidator(roomRepository);
    }

    @Test
    void testValidateWhenRoomIsValid() {
        victim.validate(newCreateRoomDTO());
    }

    @Test
    void testValidateWhenRoomNameIsMissing() {
        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().name(null)));

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomNameExceedsLength() {
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().name(StringUtils.rightPad("X", ROOM_NAME_MAX_LENGTH + 1, 'X'))));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + EXCEEDS_MAX_LENGTH), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomSeatsAreMissing() {
        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(null)));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_SEATS, ROOM_SEATS + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomSeatsAreLessThanMinValue() {
        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(ROOM_SEATS_MIN_VALUE - 1)));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_SEATS, ROOM_SEATS + BELOW_MIN_VALUE), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomSeatsAreGreaterThanMaxValue() {
        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(ROOM_SEATS_MAX_VALUE + 1)));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_SEATS, ROOM_SEATS + EXCEEDS_MAX_VALUE), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomNameIsDuplicated() {
        given(roomRepository.findByNameAndActive(DEFAULT_ROOM_NAME, true))
                .willReturn(Optional.of(newRoomBuilder().build()));

//        when(roomRepository.findByNameAndActive(DEFAULT_ROOM_NAME, true)).thenReturn(Optional.of(Room.newBuilder().build()));

        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate(newCreateRoomDTO()));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + DUPLICATED), exception.getValidationErrors().getError(0));
    }
}
