###Notes
The solution runs out ot the box and contains all needed dependencies.
The database, used for this solution, is a runtime H2 instance; it should be allowed to create 
local db file in the home directory:

`~/heycardb`

It is created automatically without any configuration.

The project runs as usual Spring Boot project, with command:

`mvn clean spring-boot:run`

After the project starts, main API page can be invoked by the link:
`http://localhost:8080/swagger-ui.html`,

or with curl commands, for example:
`curl -X POST "http://localhost:8080/upload_csv/1a3e01eb-ffd8-4c08-acfe-8165524f1d8b" -H "accept: /" -H "Content-Type: multipart/form-data" -F "file=@listings.csv;type=text/csv"`

###Assumptions
1. Create 2 entities (Dealer, Listing);
2. Use /vehicle_listings/ with id (I thought it was implied);
3. Don't reveal internal db ids, use only generated UUID safe to be shown;
4. _Normally_ the dealers should be registered (and get their UUIDs) before being able to send the listings,
the assumption was to create dealers just when they send the listings - that was made for simplification of the task _only_;
5. incomplete cvs listings are skipped and exceptions just logged in console;
6. short/SMALLINT type could be used for kW, year, power-in-ps but assumption was we don't have so much  big data for the task;
7. Don't use BigDecimal for the price, as it is always in the highest common unit;
8. Use POST for update - sometimes it is acceptable;
9. Use Long/GenerationType.IDENTITY to workaround H2 issue (but SEQUENCE with appropriate allocationSize usually works better).

###Challenges
The main task was to allow update for existing listings (by code) for the dealers specified. For this I had to create a map with 
codes as keys per a dealer. Another thing to think about was a task to upload the file when you use WebFlux instead of MVC.
And the last task was a bug in Swagger when it doesn't want to show Upload button on the UI page.

###Tests
The project has unit tests wherever it is sensible, but the coverage is not ideal. There is an integration test for 
the main functionality (controller). I didn't make better cover because of the time.

###Ideas to implement
- Dealers should be registered before using this API. For this, new endpoint has to be created and should
return the UUID, that is not guessable as internal ids.
- The dealer probebly should know only about the listings he or she has sent, otherwise any person could see
and potentially maliciously update the listing. For this, some authentification/authorization should be implemented.
- As I had no guess about the amount of data, it would be better to review the code, having the actual amounts in mind.

###Decisions and architecture
I decided to use WebFlux instead of MVC as it asynchronos and non-blocking and potentially could be more performant than latter.
The only bottleneck could be at DB layer (there's no async reliable DB drivers at the moment).
Another decision was to use H2, as it is in-memory DB and doesn't need any setup. I could use docker container, but H2
seemed simplier option to setup and run.

I will be able to answer any further questions on this project if they are.

