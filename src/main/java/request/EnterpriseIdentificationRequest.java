package request;


public record EnterpriseIdentificationRequest (
         String CNPJ,
         String name,
         String address,
         Integer number,
         String complement,
         String country,
         String State,
         String county
){}
