package ru.urururu.tests.revolut;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
@Path("api/v1/accounts")
@Produces("application/json")
public class AccountService {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final AccountRepository repository = AccountRepository.getInstance();

    @GET
    public Collection<Account> list() {
        return repository.list();
    }

    @POST
    public Account add() {
        return repository.add();
    }

    @Path("{id}/balance")
    @PUT
    public void setBalance(@PathParam("id") long id, @FormParam("value") long balance) {
        repository.setBalance(id, balance);
    }

    @Path("{from}/transfers")
    @POST
    public long transfer(@PathParam("from") long from, @FormParam("to") long to, @FormParam("amount") long amount) {
        try {
            return repository.transfer(from, to, amount);
        } catch (IllegalArgumentException e) {
            logger.warning(() -> toMessage(e));
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(e.getMessage())
                    .build());
        } catch (Exception e) {
            logger.severe(() -> toMessage(e));
            throw new WebApplicationException(e);
        }
    }

    private String toMessage(Throwable e) {
        StringWriter sw /* safe for work */ = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
