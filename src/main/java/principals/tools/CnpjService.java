package principals.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import response.EmpresaResponse;

public class CnpjService {

    public EmpresaResponse buscarEmpresaPorCnpj(String cnpj) throws IOException {
        String url = "https://open.cnpja.com/office/" + cnpj;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String jsonResponse = reader.lines().collect(Collectors.joining());
            JSONObject jsonObject = new JSONObject(jsonResponse);

            return new EmpresaResponse(
                    jsonObject.optString("updated"),
                    jsonObject.optString("taxId"),
                    jsonObject.optString("alias"),
                    jsonObject.optString("founded"),
                    jsonObject.optBoolean("head"),
                    parseCompany(jsonObject.getJSONObject("company")),
                    jsonObject.optString("statusDate"),
                    parseStatus(jsonObject.getJSONObject("status")),
                    parseAddress(jsonObject.getJSONObject("address")),
                    parseMainActivity(jsonObject.getJSONObject("mainActivity")),
                    parsePhones(jsonObject.getJSONArray("phones")),
                    parseEmails(jsonObject.getJSONArray("emails")),
                    parseSideActivities(jsonObject.getJSONArray("sideActivities")),
                    jsonObject.optJSONArray("registrations").toList(),
                    jsonObject.optJSONArray("suframa").toList()
            );
        }
    }

    private EmpresaResponse.Company parseCompany(JSONObject jsonObject) {
        return new EmpresaResponse.Company(
                parseMembers(jsonObject.getJSONArray("members")),
                jsonObject.optLong("id"),
                jsonObject.optString("name"),
                jsonObject.optInt("equity"),
                new EmpresaResponse.Nature(jsonObject.getJSONObject("nature").optInt("id"), jsonObject.getJSONObject("nature").optString("text")),
                new EmpresaResponse.Size(jsonObject.getJSONObject("size").optInt("id"), jsonObject.getJSONObject("size").optString("acronym"), jsonObject.getJSONObject("size").optString("text")),
                new EmpresaResponse.Simples(jsonObject.getJSONObject("simples").optBoolean("optant"), jsonObject.getJSONObject("simples").optString("since")),
                new EmpresaResponse.Simei(jsonObject.getJSONObject("simei").optBoolean("optant"), jsonObject.getJSONObject("simei").optString("since"))
        );
    }

    private List<EmpresaResponse.Member> parseMembers(JSONArray jsonArray) {
        List<EmpresaResponse.Member> members = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject memberJson = jsonArray.getJSONObject(i);
            members.add(new EmpresaResponse.Member(
                    memberJson.optString("since"),
                    parsePerson(memberJson.getJSONObject("person")),
                    new EmpresaResponse.Role(memberJson.getJSONObject("role").optInt("id"), memberJson.getJSONObject("role").optString("text"))
            ));
        }
        return members;
    }

    private EmpresaResponse.Person parsePerson(JSONObject jsonObject) {
        return new EmpresaResponse.Person(
                jsonObject.optString("id"),
                jsonObject.optString("type"),
                jsonObject.optString("name"),
                jsonObject.optString("taxId"),
                jsonObject.optString("age")
        );
    }

    private EmpresaResponse.Status parseStatus(JSONObject jsonObject) {
        return new EmpresaResponse.Status(
                jsonObject.optInt("id"),
                jsonObject.optString("text")
        );
    }

    private EmpresaResponse.Address parseAddress(JSONObject jsonObject) {
        return new EmpresaResponse.Address(
                jsonObject.optInt("municipality"),
                jsonObject.optString("street"),
                jsonObject.optString("number"),
                jsonObject.optString("district"),
                jsonObject.optString("city"),
                jsonObject.optString("state"),
                jsonObject.optString("details"),
                jsonObject.optString("zip"),
                new EmpresaResponse.Country(jsonObject.getJSONObject("country").optInt("id"), jsonObject.getJSONObject("country").optString("name"))
        );
    }

    private EmpresaResponse.MainActivity parseMainActivity(JSONObject jsonObject) {
        return new EmpresaResponse.MainActivity(
                jsonObject.optInt("id"),
                jsonObject.optString("text")
        );
    }

    private List<EmpresaResponse.Phone> parsePhones(JSONArray jsonArray) {
        List<EmpresaResponse.Phone> phones = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject phoneJson = jsonArray.getJSONObject(i);
            phones.add(new EmpresaResponse.Phone(
                    phoneJson.optString("area"),
                    phoneJson.optString("number")
            ));
        }
        return phones;
    }

    private List<EmpresaResponse.Email> parseEmails(JSONArray jsonArray) {
        List<EmpresaResponse.Email> emails = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject emailJson = jsonArray.getJSONObject(i);
            emails.add(new EmpresaResponse.Email(
                    emailJson.optString("address"),
                    emailJson.optString("domain")
            ));
        }
        return emails;
    }

    private List<EmpresaResponse.SideActivity> parseSideActivities(JSONArray jsonArray) {
        List<EmpresaResponse.SideActivity> sideActivities = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject activityJson = jsonArray.getJSONObject(i);
            sideActivities.add(new EmpresaResponse.SideActivity(
                    activityJson.optInt("id"),
                    activityJson.optString("text")
            ));
        }
        return sideActivities;
    }
}
