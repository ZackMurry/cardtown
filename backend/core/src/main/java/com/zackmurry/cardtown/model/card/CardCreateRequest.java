package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.LengthRequiredException;
import com.zackmurry.cardtown.exception.PayloadTooLargeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest implements EmailOwnerCard {

    private static final Logger logger = LoggerFactory.getLogger(CardCreateRequest.class);

    private String ownerEmail; // is not included in the http request (if it is, it is ignored)
    private String tag;
    private String cite;
    private String citeInformation;
    private String bodyHtml;
    private String bodyDraft;
    private String bodyText;

    public CardEntity toCardEntity(UUID ownerId) {
        return new CardEntity(
                ownerId,
                tag,
                cite,
                citeInformation,
                bodyHtml,
                bodyDraft,
                bodyText
        );
    }

    /**
     * Validates fields to make sure that this request is valid.
     * If an optional field is null, this will replace it with an empty string.
     *
     * @throws BadRequestException      If any of the following are null: bodyHtml, bodyDraft, bodyText, and cite
     * @throws LengthRequiredException  If the tag is longer than 256 characters, the cite is longer than 128 characters, or citeInformation is longer than 2048 characters.
     * @throws PayloadTooLargeException If any of the body fields are too large
     */
    public void validateFields() {
        if (bodyHtml == null ||
                bodyDraft == null ||
                bodyText == null ||
                cite == null) {
            throw new BadRequestException();
        }
        if (tag == null) {
            tag = "";
        }
        if (citeInformation == null) {
            citeInformation = "";
        }
        if (tag.length() > 256 || tag.length() == 0 || cite.length() > 128 || cite.length() == 0 || citeInformation.length() > 2048) {
            throw new LengthRequiredException();
        }

        // These lengths would pretty much only occur maliciously
        if (bodyHtml.length() > 100000 || bodyText.length() > 15000 || bodyDraft.length() > 50000) {
            logger.warn("Encountered a large card body payload. Lengths - Html: {}, Text: {}, draft: {}", bodyHtml.length(), bodyText.length(), bodyDraft.length());
            throw new PayloadTooLargeException();
        }
    }

}
