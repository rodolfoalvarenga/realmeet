package br.com.sw2you.realmeet.exception;

import br.com.sw2you.realmeet.validator.ValidationError;

import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_ID;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.IN_THE_PAST;

public class AllocationCannotBeDeletedException extends InvalidRequestException {

    public AllocationCannotBeDeletedException() {
        super(new ValidationError(ALLOCATION_ID, ALLOCATION_ID + IN_THE_PAST));
    }
}
