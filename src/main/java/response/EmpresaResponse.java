package response;

import java.util.List;

public record EmpresaResponse(
        String updated,
        String taxId,
        String alias,
        String founded,
        boolean head,
        Company company,
        String statusDate,
        Status status,
        Address address,
        MainActivity mainActivity,
        List<Phone> phones,
        List<Email> emails,
        List<SideActivity> sideActivities,
        List<Object> registrations,
        List<Object> suframa
) {public record Company(
        List<Member> members,
        long id,
        String name,
        int equity,
        Nature nature,
        Size size,
        Simples simples,
        Simei simei) {}

    public record Member(
            String since,
            Person person,
            Role role
    ) {}

    public record Person(
            String id,
            String type,
            String name,
            String taxId,
            String age
    ) {}

    public record Role(
            int id,
            String text
    ) {}

    public record Nature(
            int id,
            String text
    ) {}

    public record Size(
            int id,
            String acronym,
            String text
    ) {}

    public record Simples(
            boolean optant,
            String since
    ) {}

    public record Simei(
            boolean optant,
            String since
    ) {}

    public record Status(
            int id,
            String text
    ) {}

    public record Address(
            int municipality,
            String street,
            String number,
            String district,
            String city,
            String state,
            String details,
            String zip,
            Country country
    ) {}

    public record Country(
            int id,
            String name
    ) {}

    public record MainActivity(
            int id,
            String text
    ) {}

    public record Phone(
            String area,
            String number
    ) {}

    public record Email(
            String address,
            String domain
    ) {}

    public record SideActivity(
            int id,
            String text
    ) {}

}


