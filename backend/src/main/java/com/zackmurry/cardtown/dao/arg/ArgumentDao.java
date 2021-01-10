package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArgumentDao {

    Optional<UUID> createArgument(ArgumentCreateRequest request);

    Optional<ArgumentEntity> getArgument(UUID id);

}
