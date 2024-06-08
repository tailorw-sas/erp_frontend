package com.kynsoft.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MailJetVar {
    public String Key;
    public Object Value;

    public static JSONObject createVarsJsonObject(List<MailJetVar> vars) throws JSONException {
        JSONObject varsJsonObject = new JSONObject();

        for (MailJetVar var : vars) {
            varsJsonObject.put(var.getKey(), var.getValue());
        }

        return varsJsonObject;
    }
}
