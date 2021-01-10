package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.arg.ArgumentDao;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArgumentService {

    @Autowired
    private ArgumentDao argumentDao;

    @Autowired
    private UserService userService;

    public ResponseEntity<String> createArgument(ArgumentCreateRequest request) {
        if (request == null || request.getName() == null || request.getOwnerId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getName().length() > 128) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        try {
            request.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final UUID argId = argumentDao.createArgument(request).orElse(null);
        if (argId == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final String compressedArgId = UUIDUtils.compress(argId);
        return new ResponseEntity<>(compressedArgId, HttpStatus.OK);
    }

    public ResponseEntity<ResponseArgument> getResponseArgumentById(@NonNull String id) {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        final byte[] secretKey = principal.getSecretKey();

        final UUID uuidId = UUIDUtils.decompress(id);
        final ArgumentEntity argumentEntity = argumentDao.getArgument(uuidId).orElse(null);
        if (argumentEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!argumentEntity.getOwnerId().equals(userId)) {
            // todo sharing
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            argumentEntity.decryptFields(secretKey);
        } catch (Exception e) {
            // this happens if the encoding is invalid for some reason
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final User owner = userService.getUserById(argumentEntity.getOwnerId()).orElse(null);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.GONE);
        }
        final ResponseArgument responseArgument = ResponseArgument.fromArgumentEntity(argumentEntity, ResponseUserDetails.fromUser(owner));
        return new ResponseEntity<>(responseArgument, HttpStatus.OK);
    }
}
