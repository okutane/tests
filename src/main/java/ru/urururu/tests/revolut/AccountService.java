package ru.urururu.tests.revolut;

import javax.ws.rs.*;
import java.util.Collection;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
@Path("api/v1/accounts")
@Produces("application/json")
public class AccountService {
    private final AccountRepository repository = AccountRepository.getInstance();

    @GET
    public Collection<Account> list() {
        return repository.list();
    }

    @POST
    public void add() {

    }

    @Path("{from}/transfers")
    @POST
    public void transfer(@PathParam("from") long from, @QueryParam("to") long to, @QueryParam("amount") long amount) {
        repository.transfer(from, to, amount);
    }
}
