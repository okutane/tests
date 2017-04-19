package ru.urururu.tests.revolut;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class AccountRepository {
    AtomicLong nextAccountId = new AtomicLong(1);
    Map<Long, Account> accounts = new ConcurrentHashMap<>();

    private AccountRepository() {
    }

    public Account add() {
        Account account = new Account(nextAccountId.getAndIncrement());

        accounts.put(account.getId(), account);

        return account;
    }

    public Collection<Account> list() {
        return accounts.values();
    }

    public static AccountRepository getInstance() {
        return Holder.INSTANCE;
    }

    public void credit(long accountId, long amount) {
        Account account = accounts.get(accountId);

        synchronized (account) {
            account.setBalance(account.getBalance() + amount);
        }
    }

    public void transfer(long fromId, long toId, long amount) {
        if (fromId > toId) {
            // ordering ids, to prevent deadlocks
            long temp = fromId;
            fromId = toId;
            toId = temp;
            amount = -amount;
        }

        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        if (from == null) {
            throw new IllegalArgumentException("no user with id: " + fromId);
        }
        if (to == null) {
            throw new IllegalArgumentException("no user with id: " + toId);
        }

        synchronized (from) {
            synchronized (to) {
                if (from.getBalance() - amount < 0) {
                    throw new IllegalArgumentException("balance too low for: " + fromId);
                }

                if (to.getBalance() + amount < 0) {
                    throw new IllegalArgumentException("balance too low for: " + toId);
                }

                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);
            }
        }
    }

    private static class Holder {
        static final AccountRepository INSTANCE = new AccountRepository();
    }
}
