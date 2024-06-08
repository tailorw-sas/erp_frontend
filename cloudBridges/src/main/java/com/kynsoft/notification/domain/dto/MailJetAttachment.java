package com.kynsoft.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MailJetAttachment {
    String contentType;
    String fileName;
    String base64Content;

    public static JSONArray generateAttachments(List<MailJetAttachment> mailJetAttachments) throws JSONException {
        JSONArray attachments = new JSONArray();

        if (mailJetAttachments == null || mailJetAttachments.isEmpty()) {
            return attachments;
        }

        for (MailJetAttachment mailJetAttachment : mailJetAttachments) {
            JSONObject attachment = new JSONObject();
            attachment.put("Content-type", mailJetAttachment.getContentType());
            attachment.put("Filename", mailJetAttachment.getFileName());
            attachment.put("content", mailJetAttachment.getBase64Content());
            attachments.put(attachment);
        }

        return attachments;
    }
}