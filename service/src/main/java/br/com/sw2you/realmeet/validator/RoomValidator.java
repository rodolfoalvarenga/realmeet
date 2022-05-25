package br.com.sw2you.realmeet.validator;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateRoomDTO;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static br.com.sw2you.realmeet.validator.ValidatorConstants.DUPLICATED;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_ID;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS_MAX_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS_MIN_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.throwOnError;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateMaxLength;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateMaxValue;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateMinValue;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateRequired;
import static java.util.Objects.isNull;

@Component
public class RoomValidator {

    private final RoomRepository roomRepository;

    public RoomValidator(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void validate(CreateRoomDTO createRoomDTO) {
        var validationErrors = new ValidationErrors();

        if (validateName(createRoomDTO.getName(), validationErrors) &&
                validateSeats(createRoomDTO.getSeats(), validationErrors))
        {
            validateNameDuplicated(null, createRoomDTO.getName(), validationErrors);
        }

        throwOnError(validationErrors);
    }

    public void validate(Long roomId, UpdateRoomDTO updateRoomDTO) {
        var validationErrors = new ValidationErrors();

        if (validateRequired(roomId, ROOM_ID, validationErrors) &&
                validateName(updateRoomDTO.getName(), validationErrors) &&
                validateSeats(updateRoomDTO.getSeats(), validationErrors))
        {
            validateNameDuplicated(roomId, updateRoomDTO.getName(), validationErrors);
        }

        throwOnError(validationErrors);
    }

    private boolean validateName(String name, ValidationErrors validationErrors) {
        return (validateRequired(name, ROOM_NAME, validationErrors) &&
                validateMaxLength(name, ROOM_NAME, ROOM_NAME_MAX_LENGTH, validationErrors)
        );
    }

    private boolean validateSeats(Integer seats, ValidationErrors validationErrors) {
        return (validateRequired(seats, ROOM_SEATS, validationErrors) &&
                validateMinValue(seats, ROOM_SEATS, ROOM_SEATS_MIN_VALUE, validationErrors) &&
                validateMaxValue(seats, ROOM_SEATS, ROOM_SEATS_MAX_VALUE, validationErrors)
        );
    }

    private void validateNameDuplicated(Long roomIdToExclude, String name, ValidationErrors validationErrors) {
        roomRepository
                .findByNameAndActive(name, true)
                .ifPresent(room -> {
                    if (isNull(roomIdToExclude) || !Objects.equals(room.getId(), roomIdToExclude)) {
                        validationErrors.add(ROOM_NAME, ROOM_NAME + DUPLICATED);
                    }
                });
    }
}
