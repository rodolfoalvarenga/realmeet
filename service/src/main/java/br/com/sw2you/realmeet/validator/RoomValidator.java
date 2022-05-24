package br.com.sw2you.realmeet.validator;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.springframework.stereotype.Component;

import static br.com.sw2you.realmeet.validator.ValidatorConstants.DUPLICATED;
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
            validateNameDuplicated(createRoomDTO.getName(), validationErrors);
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

    private void validateNameDuplicated(String name, ValidationErrors validationErrors) {
        roomRepository
                .findByNameAndActive(name, true)
                .ifPresent(__ -> // __ define a variable that will never be used
                    validationErrors.add(ROOM_NAME, ROOM_NAME + DUPLICATED));
    }
}
