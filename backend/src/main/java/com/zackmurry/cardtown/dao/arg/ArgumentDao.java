package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;

import java.util.Optional;
import java.util.UUID;

public interface ArgumentDao {

    Optional<UUID> createArgument(ArgumentCreateRequest request);

}
