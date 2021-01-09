package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.arg.ArgumentDao;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArgumentService {

    @Autowired
    private ArgumentDao argumentDao;

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

}
